Add-Type -AssemblyName System.Windows.Forms

while ($true) {
    [System.Windows.Forms.SendKeys]::SendWait("{SCROLLLOCK}")
    Start-Sleep -Seconds 1
    [System.Windows.Forms.SendKeys]::SendWait("{SCROLLLOCK}")
    Start-Sleep -Seconds 300  # Espera 5 minutos antes de repetir
}
