# En PowerShell
Remove-Item -Recurse -Force target
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository\io\quarkus"
mvn clean install -U
