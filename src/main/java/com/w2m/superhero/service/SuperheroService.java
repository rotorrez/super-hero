AppianCaseAndGoService
Provides high-level methods to interact with Appian Case-and-Go (ANDGO): start a fast process and complete tasks.
Uses AppianCaseAndGoRestClient and the shared TokenProvider.
Requires a separate base URL/config from the existing Appian “events” client.

Methods:

StartFastProcessResponseDTO startFastProcess(String processCode, StartFastProcessRequestDTO request) – Starts a case and returns the next available tasks.
