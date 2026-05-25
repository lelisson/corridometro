# Gera ic_launcher* a partir de PNG quadrado (fundo verde + arte).
param(
    [string]$Source = (Join-Path $PSScriptRoot "app\designs\logo\corridometro-logo-pneu-medidor.png")
)

Add-Type -AssemblyName System.Drawing

function Save-ResizedPng {
    param([string]$Src, [string]$Dest, [int]$Size)
    $dir = Split-Path $Dest -Parent
    if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    $img = [System.Drawing.Image]::FromFile((Resolve-Path $Src))
    $bmp = New-Object System.Drawing.Bitmap $Size, $Size
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.Clear([System.Drawing.Color]::FromArgb(255, 22, 163, 74))
    $g.DrawImage($img, 0, 0, $Size, $Size)
    $g.Dispose()
    $bmp.Save($Dest, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    $img.Dispose()
    Write-Host "  $Size px -> $Dest"
}

if (-not (Test-Path $Source)) {
    Write-Error "Arquivo nao encontrado: $Source"
    exit 1
}

$res = Join-Path $PSScriptRoot "app\src\main\res"
Write-Host "Fonte: $Source"
Save-ResizedPng $Source (Join-Path $res "drawable-nodpi\ic_launcher_foreground.png") 432
Save-ResizedPng $Source (Join-Path $res "mipmap-mdpi\ic_launcher.png") 48
Save-ResizedPng $Source (Join-Path $res "mipmap-mdpi\ic_launcher_round.png") 48
Save-ResizedPng $Source (Join-Path $res "mipmap-hdpi\ic_launcher.png") 72
Save-ResizedPng $Source (Join-Path $res "mipmap-hdpi\ic_launcher_round.png") 72
Save-ResizedPng $Source (Join-Path $res "mipmap-xhdpi\ic_launcher.png") 96
Save-ResizedPng $Source (Join-Path $res "mipmap-xhdpi\ic_launcher_round.png") 96
Save-ResizedPng $Source (Join-Path $res "mipmap-xxhdpi\ic_launcher.png") 144
Save-ResizedPng $Source (Join-Path $res "mipmap-xxhdpi\ic_launcher_round.png") 144
Save-ResizedPng $Source (Join-Path $res "mipmap-xxxhdpi\ic_launcher.png") 192
Save-ResizedPng $Source (Join-Path $res "mipmap-xxxhdpi\ic_launcher_round.png") 192
Copy-Item $Source (Join-Path $PSScriptRoot "app\designs\logo\corridometro-logo.png") -Force
Write-Host "Icone atualizado."
