start /B ant clean
timeout /t 1
start /B ant run-processHistoryFiles 
timeout /t 2
cls
copy calicoHistory_2012_08_22_14_57_20.chist dist\historyrecorderplugin-trunk
ant run-processHistoryFiles