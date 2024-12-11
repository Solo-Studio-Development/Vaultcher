
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
- `/vaultcher create <name> <uses> <command>` - Creates a new vaultcher.
- `/vaultcher menu` - Opens the vaultcher menu.
- `/vaultcher delete <name>` - Deletes the vaultcher.
- `/vaultcher edituse <name> <new use>` - Edits the uses of the vaultcher.
- `/vaultcher editname <name> <new name>` - Edits the name of the vaultcher.
- `/vaultcher editcommand <name> <new command>` - Edits the command of the vaultcher.
- `/vaultcher add <name> <target>` - Adds a permission to the vaultcher.
- `/vaultcher give <name> <target>` - Gives a permission to the vaultcher.
- `/vaultcher redeem <name>` - Redeems the vaultcher.

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

## 🤝 Contributing

Contributions are always welcome! If you have ideas for improvements, feel free to join the Discord server and open a ticket. Let's build something awesome together!

---

## 📜 License

This plugin is licensed under the [Apache-2.0 License](https://www.apache.org/licenses/LICENSE-2.0).