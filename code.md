# Modified Code
## About
Because I can't commit the full source code for a lot of files as the code is owned by Mojang I have created this file to help track the changes I made in the files.

## Minecraft.java
### Location
`net.minecraft.client`

### Changes

**Additions**

```
...
this.ingameGUI = new GuiIngame(this);
        
// Huzuni
Huzuni.INSTANCE.start(getMinecraft(), mcDataDir);

if (this.serverName != null)
{
...
```