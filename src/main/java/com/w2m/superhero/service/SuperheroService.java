The eventMap must contain an action, a data map, and optionally an accessPointId. The data map must include:

Key	Type	Required	Description
caseId	String	✅	Internal case ID
processId	String	✅	Process identifier
userId	String	✅	User triggering the event
centerId	String	✅	Execution center
stageId	String	⛔	(Optional) Stage to assign
accessPointId	String	⛔	(Optional) Overridden in root level

📤 Behavior
Validates required fields.

Automatically applies default status (CTFO_E_08) and isFinal = false.
