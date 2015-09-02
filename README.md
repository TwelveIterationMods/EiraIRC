EiraIRC
=======

Minecraft Mod. On a Minecraft client it acts like any other IRC client, on the server it turns into a bridge-bot. Configuration is highly customizable and the mod includes a few special features such as name colors, Twitch chat and screenshot upload.

##Useful Links
* [Latest Builds](http://jenkins.blay09.net) on my Jenkins
* [Minecraft Forum Topic](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1291581-eirairc-irc-twitch-screenshots-and-more) for discussion, support and feature requests 
* [Help and Documentation Page](http://blay09.net/?page_id=63)

##API
The easiest way to add EiraIRC to your development environment is to do some additions to your build.gradle file. First, register EiraIRC's maven repository by adding the following lines:

```
repositories {
    maven {
        name = "eiranet"
        url ="http://repo.blay09.net"
    }
}
```

Then, add a dependency to either just the EiraIRC API (api) or, if you want EiraIRC to be available while testing as well, the deobfuscated version (dev):

```
dependencies {
    compile 'net.blay09.mods:eirairc:major.minor.build:dev' // or just api instead of dev
}
```

*Important*: If you do use the dev version like that, make sure that you still only use code within the API packages! Rikka will get mad at you and give you a dose of Schwarz Sechs if you mess with any of EiraIRC's internal classes.

Make sure you enter the correct version number for the Minecraft version you're developing for. The major version is the important part here; it is increased for every Minecraft update.  See the jenkins to find out the latest version number.

Done! Run gradle to update your project and you'll be good to go.

The latest EiraIRC API and an unobfuscated version of the mod can also be downloaded from my [Jenkins](http://jenkins.blay09.net) (v2.8 only), if you're not into all that maven stuff.

##Repacking Textures
EiraIRC reads its UI textures from a [libGDX](http://libgdx.badlogicgames.com/) TextureAtlas.
The repository root contains a project file for the [libGDX TexturePacker GUI](https://code.google.com/p/libgdx-texturepacker-gui/).
As of now, only a TextureAtlas of size 256x256 is supported.