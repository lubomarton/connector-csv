param(
    [string]$Path = "src/main/java/com/evolveum/polygon/connector/csv/ObjectClassHandler.java"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $Path)) {
    throw "ObjectClassHandler not found: $Path"
}

$encoding = [System.Text.UTF8Encoding]::new($false)
$text = [System.IO.File]::ReadAllText((Resolve-Path -LiteralPath $Path), $encoding)
$newline = if ($text.Contains("`r`n")) { "`r`n" } else { "`n" }
$normalized = $text.Replace("`r`n", "`n")

function Normalize-Block([string]$value) {
    return $value.Replace("`r`n", "`n").TrimStart("`n")
}

function Replace-ExactlyOnce([string]$source, [string]$oldValue, [string]$newValue, [string]$label) {
    $oldValue = Normalize-Block $oldValue
    $newValue = Normalize-Block $newValue
    $matches = [regex]::Matches($source, [regex]::Escape($oldValue)).Count
    if ($matches -ne 1) {
        throw "$label: expected exactly one match, found $matches. Refusing to modify the file."
    }
    return $source.Replace($oldValue, $newValue)
}

$oldSetup = @'
			CSVParser parser = csv.parse(reader);
			boolean shouldReiterate = false;
			Iterator<CSVRecord> iterator = parser.iterator();

			HashMap <ConnectorObjectId, CandidateSet<ConnectorObjectCandidate>> candidates = new HashMap<>();
'@

$newSetup = @'
			CSVParser parser = csv.parse(reader);
			Iterator<CSVRecord> iterator = parser.iterator();

			HashMap <ConnectorObjectId, CandidateSet<ConnectorObjectCandidate>> candidates = new HashMap<>();
			Map<ConnectorObjectId, ConnectorObjectCandidate> candidatesByOwnId = new HashMap<>();
'@

$normalized = Replace-ExactlyOnce $normalized $oldSetup $newSetup "executeQuery setup"

$oldScan = @'
						ConnectorObjectId cid =  ob.getId();
						saturateCandidates(cid, candidates, ob);

						if (!shouldReiterate) {

							shouldReiterate = appendToCandidateMap(ob, candidates, true);
						} else {

							appendToCandidateMap(ob, candidates, true);
						}
'@

$newScan = @'
						ConnectorObjectId cid =  ob.getId();
						candidatesByOwnId.put(cid, ob);
						saturateCandidates(cid, candidates, ob);
						appendToCandidateMap(ob, candidates, true);
'@

$normalized = Replace-ExactlyOnce $normalized $oldScan $newScan "executeQuery candidate scan"

$oldPostScan = @'
			if (!ArrayUtils.isEmpty(configuration.getManagedAssociationPairs())) {
				if (shouldReiterate) {

					reIterateCandidates(candidates);
				}

				retrieveAssociationData(candidates);
'@

$newPostScan = @'
			if (!ArrayUtils.isEmpty(configuration.getManagedAssociationPairs())) {
				saturateSameClassCandidates(candidatesByOwnId);
				retrieveAssociationData(candidates);
'@

$normalized = Replace-ExactlyOnce $normalized $oldPostScan $newPostScan "executeQuery post-scan resolution"

$anchor = @'
	private void saturateCandidates(ConnectorObjectId cid,
'@

$helper = @'
	private void saturateSameClassCandidates(Map<ConnectorObjectId, ConnectorObjectCandidate> candidatesByOwnId) {
		for (ConnectorObjectCandidate candidate : candidatesByOwnId.values()) {
			for (ConnectorObjectId objectId : new HashSet<>(candidate.getObjectIdsToBeProcessed())) {
				if (!getObjectClass().equals(objectId.getObjectClass())) {
					continue;
				}

				ConnectorObjectCandidate referencedCandidate = candidatesByOwnId.get(objectId);
				if (referencedCandidate != null) {
					candidate.addCandidateUponWhichThisDepends(referencedCandidate);
				}
			}
		}
	}

	private void saturateCandidates(ConnectorObjectId cid,
'@

$normalized = Replace-ExactlyOnce $normalized $anchor $helper "same-class resolver insertion"

if ($newline -eq "`r`n") {
    $output = $normalized.Replace("`n", "`r`n")
} else {
    $output = $normalized
}

[System.IO.File]::WriteAllText((Resolve-Path -LiteralPath $Path), $output, $encoding)
Write-Host "Applied guarded same-class executeQuery index patch to $Path"
