!macro MUI_LANGUAGE_GETCOND LanguageID OutVar
  Push "${LanguageID}"
  Call GetDlgItem
  Pop ${OutVar}
!macroend

!macro MUI_LANGUAGE_LOADDLL hInstance OutVar
  Push $0
  Push $1
  Push $2
  Push $3
  
  StrCpy $0 ""
  FindFirst $1 $2 $PLUGINSDIR\Langstrings_*.nsh
  StrCmp $2 "" end
  loop:
    StrCpy $3 $2 15 0
    StrCmp $3 "Langstrings_" 0 next
    StrCpy $3 $2 2 15
    StrCmp $3 "10" 0 next
    StrCpy $3 $2 3 17
    strcmp $3 "ENU" 0 next
    strcpy $0 $2
    goto end
  next:
    FindNext $1 $2
    StrCmp $2 "" end
    goto loop
  end:
    FindClose $1
    StrCpy ${OutVar} $0
!macroend