function Get-CorridometroRoot {
    $dir = $PSScriptRoot
    while ($dir) {
        if (Test-Path (Join-Path $dir "gradlew.bat")) {
            return (Resolve-Path $dir).Path
        }
        $parent = Split-Path $dir -Parent
        if (-not $parent -or $parent -eq $dir) {
            break
        }
        $dir = $parent
    }
    throw "Raiz do projeto Corridometro nao encontrada (gradlew.bat)."
}

function Set-CorridometroJavaHome {
    $studioJbr = "C:\Program Files\Android\Android Studio\jbr"
    if (Test-Path "$studioJbr\bin\java.exe") {
        $env:JAVA_HOME = $studioJbr
    } elseif (-not $env:JAVA_HOME) {
        throw "JAVA_HOME nao definido. Instale o Android Studio ou configure JAVA_HOME."
    }
}

function Get-AppVersionFromGradle {
    param([string]$Root)
    $gradle = Get-Content (Join-Path $Root "app\build.gradle.kts") -Raw
    if ($gradle -notmatch '(?s)defaultConfig\s*\{[^}]*versionCode\s*=\s*(\d+)[^}]*versionName\s*=\s*"([^"]+)"') {
        throw "Nao foi possivel ler versionCode/versionName de app/build.gradle.kts"
    }
    return @{ Code = $matches[1]; Name = $matches[2] }
}
