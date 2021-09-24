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

lp creategroup admin
lp group admin permission set luckperms.* true
lp group admin permission set spligotplugintest.poweroff true
```
- Assign yourself to `admin` group
```
lp user <YOUR_USERNAME> parent add admin
```
