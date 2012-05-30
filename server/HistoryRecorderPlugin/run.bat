start /B ant clean
timeout /t 1
start /B ant run-processHistoryFiles 
timeout /t 2
cls
copy calicoHistory_2012_05_03_06_47_18.chist dist\historyrecorderplugin-trunk
ant run-processHistoryFiles