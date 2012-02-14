start /B ant clean
timeout /t 1
start /B ant run-processHistoryFiles 
timeout /t 2
cls
copy calicoHistory_2011_11_15_20_08_04.chist dist\historyrecorderplugin-trunk
ant run-processHistoryFiles