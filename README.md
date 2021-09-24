## Prerequisites
- Make sure Java 16 is installed
- Download PaperMC server `1.17.1` from https://papermc.io/downloads
- Run it for the first time to generate directory structure `java -Xms2G -Xmx2G -jar paper-1.17.1-270.jar --nogui`. Accept EULA.

## Run
- Build plugin `./gradlew jar`
- Copy build artifact `build\libs\spigot-plugin-test-1.0.0.jar` to `plugins` folder of PaperMC server
- Start server with remote debugging enabled
```sh
java \
 -Xms2G -Xmx2G \
 -Xdebug \
 -Xnoagent \
 -Djava.compiler=NONE \
 -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
 -jar paper-1.17.1-270.jar \
 --nogui
```
- (Optionally) Run `Remote` configuration in IntelliJ pointing to `localhost:5005`. This will allow to set breakpoints

## (Optionally) Configure permissions
- Install LuckPerms plugin
```sh
cd ./plugins
wget https://ci.lucko.me/job/LuckPerms/1367/artifact/bukkit/loader/build/libs/LuckPerms-Bukkit-5.3.68.jar
```
- Create `admin` group and assign permission
```
lp group default permission set luckperms.* false

lp creategroup mod
lp group mod permission set minecraft.command.* true
lp group mod permission set bukkit.command.* true
lp group mod permission set minecraft.command.stop false
lp group mod permission set bukkit.command.stop false
lp group mod permission set minecraft.command.op false
lp group mod permission set bukkit.command.op false
lp group mod permission set minecraft.command.deop false
lp group mod permission set bukkit.command.deop false
lp group mod parent add default

lp creategroup admin
lp group admin permission set luckperms.* true
lp group admin permission set utilityplugin.poweroff true
lp group admin permission set minecraft.command.stop true
lp group admin permission set bukkit.command.stop true
lp group admin permission set minecraft.command.op true
lp group admin permission set bukkit.command.op true
lp group admin permission set minecraft.command.deop true
lp group admin permission set bukkit.command.deop true
lp group admin parent add mod
```
- Assign yourself to `admin` group
```
lp user <YOUR_USERNAME> parent add admin
```
- Restart server
