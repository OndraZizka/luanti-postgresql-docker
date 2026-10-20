To ensure this project remains cohesive when you move it to a permanent chat, I have synthesized our architectural journey, the technical motivations, and the final "source of truth" into a structured project document.

**Action Item:** Copy the entire block below and save it as `LuantiKotlinDSL.md`.

---

# Project: Luanti-Kotlin DSL (The "Shaded" Modding Model)

## 1. The Problem & Motivation

Standard Luanti (Minetest) modding uses Lua, a dynamically typed language. This often leads to "poorly written" mods with runtime crashes, complex table boilerplate, and difficult-to-maintain "callback hell."

**The Goal:** Build a Kotlin-based DSL that "shades" the modder from Lua entirely. The modder writes 100% typed Kotlin code, which is then compiled into optimized, standard Lua `init.lua` files.

## 2. Architectural Inspiration: KotlinPoet

Like **KotlinPoet**, this project doesn't just concatenate strings. It builds an **Intermediate Representation (IR)**—an object tree of the mod’s logic.

* **Expression Tree:** Instead of writing `..` or `:`, the modder uses Kotlin objects like `PlayerExpr`.
* **Type Safety:** The Kotlin compiler validates the "shape" of the logic (e.g., ensuring a position is passed where a position is expected).
* **Self-Contained Logic:** The "Shadow API" maps Kotlin method calls (e.g., `.getName()`) to the specific Luanti Lua strings (`:get_player_name()`).

---

## 3. Project Build Model (Maven)

The project uses Maven to manage the DSL library and execute the generator.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.luanti.kotlin</groupId>
    <artifactId>luanti-dsl-compiler</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <kotlin.version>1.9.20</kotlin.version>
        <main.class>mymod.MainKt</main.class> </properties>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>src/main/kotlin</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <goals><goal>compile</goal></goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <id>generate-lua</id>
                        <phase>prepare-package</phase>
                        <goals><goal>java</goal></goals>
                        <configuration>
                            <mainClass>${main.class}</mainClass>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

```

---

## 4. The "Shade" Infrastructure (Shadow API)

This is the core of the engine. It defines the "Shadow Objects" that represent the Lua environment.

```kotlin
// --- EXPRESSION TREE ---
interface LuaExpr { fun render(): String }

class Ref(val name: String) : LuaExpr { override fun render() = name }
class Lit(val value: Any) : LuaExpr { 
    override fun render() = if (value is String) "\"$value\"" else value.toString() 
}

class PlayerExpr(val ref: Ref) : LuaExpr {
    override fun render() = ref.render()
    fun getName() = PropertyExpr(this, "get_player_name")
    fun getHp() = PropertyExpr(this, "get_hp")
}

class PropertyExpr(val receiver: LuaExpr, val method: String) : LuaExpr {
    override fun render() = "${receiver.render()}:$method()"
}

// --- STATEMENT BUILDER ---
abstract class LuaBlock {
    val lines = mutableListOf<String>()

    fun chatAll(msg: LuaExpr) = lines.add("minetest.chat_send_all(${msg.render()})")
    fun setNode(pos: LuaExpr, node: String) = lines.add("minetest.set_node(${pos.render()}, {name='$node'})")

    // Automatic sealing via Extension on the Receiver
    fun PlayerExpr.setHp(value: Int) {
        lines.add("${this.ref.render()}:set_hp($value)")
    }

    // Logic structures
    fun checkIf(condition: LuaExpr, block: LuaBlock.() -> Unit) {
        val inner = SimpleBlock().apply(block)
        lines.add("if ${condition.render()} then\n        ${inner.render()}\n    end")
    }

    fun render() = lines.joinToString("\n        ")
}

// Concrete Scopes
class SimpleBlock : LuaBlock()
class PunchContext : LuaBlock() {
    val pos = Ref("pos") // Automatically mapped to Lua function param
    val puncher = PlayerExpr(Ref("puncher"))
}

```

---

## 5. The Modder's Result (Usage Example)

This is how a mod looks when utilizing the library. It features implicit context and type-safe helpers.

```kotlin
fun main() {
    val magicMod = NodeBuilder("magic", "cursed_stone").apply {
        description = "A stone that heals the weak but warns the strong"

        onPunch {
            // No Ref("puncher") needed; it's implicit in PunchContext
            val hp = puncher.getHp()
            
            checkIf(hp lt lit(10)) {
                puncher.setHp(20)
                chatAll(lit("You feel a surge of vitality!"))
            }

            checkIf(hp gte lit(10)) {
                chatAll(concat(lit("Be gone, "), puncher.getName()))
            }
        }
    }

    // Generator outputs the init.lua file
    val finalLua = magicMod.build()
    println(finalLua)
}

```

---

### What to do next:

1. **Copy** this entire Markdown block.
2. **Save** it to your local machine as `LuantiKotlin.md`.
3. **Start a new chat** (non-temporary) and paste this file in to continue building your "Library of Functionality" and more complex shadow objects!
