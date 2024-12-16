
![screenshot](https://i.imgur.com/Eqk8VNf.png)

Vaultcher is a powerful plugin that combines **security** (vault) with the core functionality of a **voucher**. It provides a seamless, efficient way to manage digital vouchers on your Minecraft server.

---

## 🚀 For Developers

Although the plugin is not officially listed as a library, you can still integrate it into your project by following these steps:

### 1️⃣ Create a `libs/` folder in your project directory.
### 2️⃣ Add the latest JAR file to the folder.
### 3️⃣ Import the plugin like this:

#### 📦 **Groovy Gradle**:
```groovy
dependencies {
    implementation files('libs/plugin.jar')
}
```

#### 📦 **Kotlin Gradle**:
```kotlin
implementation(files("libs/plugin.jar"))
```

#### 📦 **Maven**:
```xml
<dependencies>
    <dependency>
        <groupId>net.solostudio</groupId>
        <artifactId>vaultcher</artifactId>
        <version>{VERSION}</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/libs/plugin.jar</systemPath>
    </dependency>
</dependencies>
```

---

## 📜 Commands

- `/vaultcher reload` - Reloads the plugin.
- `/vaultcher create name: <name> uses: <uses> command: <command>` - Creates a new vaultcher.
- `/vaultcher menu` - Opens the vaultcher menu.
- `/vaultcher delete name: <name>` - Deletes the vaultcher.
- `/vaultcher edituse name: <name> new: <new use>` - Edits the uses of the vaultcher.
- `/vaultcher editname name: <name> new: <new name>` - Edits the name of the vaultcher.
- `/vaultcher editcommand name: <name> new: <new command>` - Edits the command of the vaultcher.
- `/vaultcher add name: <name> target: <target>` - Adds a permission to the vaultcher.
- `/vaultcher give name: <name> target: <target>` - Gives a permission to the vaultcher.
- `/vaultcher redeem name: <name>` - Redeems the vaultcher.
- `/vaultcher referral create` - Creates a new unique referral code.
- `/vaultcher referral redeem <name>` - Redeems the referral code.

---

## 🔑 Permissions

- `vaultcher.reload`
- `vaultcher.create`
- `vaultcher.menu`
- `vaultcher.delete`
- `vaultcher.edituse`
- `vaultcher.editname`
- `vaultcher.editcommand`
- `vaultcher.add`
- `vaultcher.give`

---

## 🔌 Placeholders

- `%v_activators%` - It shows how many people have activated your code so far.
- `%v_code%` - Shows your code.
- `%v_is_activated%` - Shows whether you have already activated a code.

---

## 🤝 Contributing

Contributions are always welcome! If you have ideas for improvements, feel free to join the Discord server and open a ticket. Let's build something awesome together!

---

## 📜 License

This plugin is licensed under the [Apache-2.0 License](https://www.apache.org/licenses/LICENSE-2.0).