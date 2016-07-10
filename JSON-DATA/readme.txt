Command to update data to driver proxy example:
curl -F "x=@json.txt;filename=a.txt;type=application/json; charset=utf-8" "http://127.0.0.1:8981/setProxyData/layout"

Command to map simulator port form PC-18981 to simulator-8981 (emulator-5554 is the device name)
adb -s emulator-5554 forward tcp:18981 tcp:8981

