cd ~/.m2/repository
find . -name "*.jar" -exec jar tf {} \; | grep QuarkusRestPathTemplate


    $search = "QuarkusRestPathTemplate"
$repo = "$env:USERPROFILE\.m2\repository"

Get-ChildItem -Path $repo -Recurse -Include *.jar | ForEach-Object {
    try {
        $jarContent = & "jar" "tf" $_.FullName 2>$null
        if ($jarContent -match $search) {
            Write-Host "FOUND in:" $_.FullName
        }
    } catch {}
}
