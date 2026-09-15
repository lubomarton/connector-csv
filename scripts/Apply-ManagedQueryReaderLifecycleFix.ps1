param(
    [string]$Path = "src/main/java/com/evolveum/polygon/connector/csv/ObjectClassHandler.java"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $Path)) {
    throw "File not found: $Path"
}

$bytes = [System.IO.File]::ReadAllBytes($Path)
$utf8 = New-Object System.Text.UTF8Encoding($false)
$text = $utf8.GetString($bytes)
$newLine = if ($text.Contains("`r`n")) { "`r`n" } else { "`n" }

function Normalize-NewLines([string]$value) {
    return ($value -replace "`r`n|`r|`n", $newLine)
}

function Replace-ExactlyOnce([string]$source, [string]$oldValue, [string]$newValue, [string]$label) {
    $oldValue = Normalize-NewLines $oldValue
    $newValue = Normalize-NewLines $newValue
    $matches = ([regex]::Matches($source, [regex]::Escape($oldValue))).Count
    if ($matches -ne 1) {
        throw "${label}: expected exactly one match, found $matches. Refusing to modify source."
    }
    return $source.Replace($oldValue, $newValue)
}

$old = @'
	@Override
	public void executeQuery(ObjectClass oc, Filter filter, ResultsHandler handler, OperationOptions oo) {
		CSVFormat csv = createCsvFormatReader(configuration);
		try (Reader reader = createReader(configuration)) {

			CSVParser parser = csv.parse(reader);
			Iterator<CSVRecord> iterator = parser.iterator();

			HashMap <ConnectorObjectId, CandidateSet<ConnectorObjectCandidate>> candidates = new HashMap<>();
			Map<ConnectorObjectId, ConnectorObjectCandidate> candidatesByOwnId = new HashMap<>();
			while (iterator.hasNext()) {
				CSVRecord record = iterator.next();
				if (skipRecord(record)) {
					continue;
				}

				if (!ArrayUtils.isEmpty(configuration.getManagedAssociationPairs())) {
					ConnectorObjectCandidate ob = createConnectorObjectOrCandidateObject(record, false);

						ConnectorObjectId cid =  ob.getId();
						candidatesByOwnId.put(cid, ob);
						saturateCandidates(cid, candidates, ob);
						appendToCandidateMap(ob, candidates, true);

				} else {
					ConnectorObject obj = createConnectorObject(record);
					if (!handleQueriedObject(filter, obj, handler)) {
						break;
					}
				}
			}

			if (!ArrayUtils.isEmpty(configuration.getManagedAssociationPairs())) {
				saturateSameClassCandidates(candidatesByOwnId);
				retrieveAssociationData(candidates);

				Set<ConnectorObjectCandidate> finalCandidateSet = new HashSet<>();
				candidates.values().forEach(val -> finalCandidateSet.addAll(val));

				for (ConnectorObjectCandidate candidate : finalCandidateSet) {

					candidate.evaluateDependencies();
					if (candidate.complete()) {

						if (!handleQueriedObject(filter, candidate.getCandidateBuilder().build(), handler)) {
							break;
						}
					} else {
						throw new ConnectorException("References of queried object were not fully resolved.");
					}
				}
			}
		} catch (Exception ex) {
			handleGenericException(ex, "Error during query execution");
		}
	}
'@

$new = @'
	@Override
	public void executeQuery(ObjectClass oc, Filter filter, ResultsHandler handler, OperationOptions oo) {
		CSVFormat csv = createCsvFormatReader(configuration);
		boolean managedAssociations = !ArrayUtils.isEmpty(configuration.getManagedAssociationPairs());

		try {
			if (!managedAssociations) {
				try (Reader reader = createReader(configuration)) {
					CSVParser parser = csv.parse(reader);
					Iterator<CSVRecord> iterator = parser.iterator();

					while (iterator.hasNext()) {
						CSVRecord record = iterator.next();
						if (skipRecord(record)) {
							continue;
						}

						ConnectorObject obj = createConnectorObject(record);
						if (!handleQueriedObject(filter, obj, handler)) {
							break;
						}
					}
				}
				return;
			}

			HashMap<ConnectorObjectId, CandidateSet<ConnectorObjectCandidate>> candidates = new HashMap<>();
			Map<ConnectorObjectId, ConnectorObjectCandidate> candidatesByOwnId = new HashMap<>();

			try (Reader reader = createReader(configuration)) {
				CSVParser parser = csv.parse(reader);
				Iterator<CSVRecord> iterator = parser.iterator();

				while (iterator.hasNext()) {
					CSVRecord record = iterator.next();
					if (skipRecord(record)) {
						continue;
					}

					ConnectorObjectCandidate ob = createConnectorObjectOrCandidateObject(record, false);
					ConnectorObjectId cid = ob.getId();
					candidatesByOwnId.put(cid, ob);
					saturateCandidates(cid, candidates, ob);
					appendToCandidateMap(ob, candidates, true);
				}
			}

			// The source CSV reader is deliberately closed before association resolution
			// and before publishing any result to ConnId. A consumer may start an update
			// as soon as handler.handle returns a result; on Windows an open source reader
			// prevents the temp-file replacement used by outbound writes.
			saturateSameClassCandidates(candidatesByOwnId);
			retrieveAssociationData(candidates);

			Set<ConnectorObjectCandidate> finalCandidateSet = new HashSet<>();
			candidates.values().forEach(val -> finalCandidateSet.addAll(val));

			for (ConnectorObjectCandidate candidate : finalCandidateSet) {
				candidate.evaluateDependencies();
				if (candidate.complete()) {
					if (!handleQueriedObject(filter, candidate.getCandidateBuilder().build(), handler)) {
						break;
					}
				} else {
					throw new ConnectorException("References of queried object were not fully resolved.");
				}
			}
		} catch (Exception ex) {
			handleGenericException(ex, "Error during query execution");
		}
	}
'@

$updated = Replace-ExactlyOnce $text $old $new "executeQuery reader lifecycle"
[System.IO.File]::WriteAllText($Path, $updated, $utf8)
Write-Host "Applied guarded managed-association query reader lifecycle fix to $Path"
