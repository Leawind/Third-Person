# 📖 Detailed Features

## Aim Mode Checking Rules

In third person view, the mod determines whether to enter aiming mode based on the items the player is holding or currently using.

During gameplay, the mod maintains two sets of [ItemPattern](./ItemPattern):

* **Hold-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player holds any item that matches any pattern in this set.
* **Use-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player is using any item that matches any pattern in this set.

_ItemPattern_ in the sets can originate from two sources:

* **mod Configuration:** _ItemPattern_ can be defined in the mod's configuration interface.
* **Resource Packs:** This includes built-in resource packs of this mod, resource packs from other mods, and manually installed resource packs by the player.

## Temporary Switch to First Person when Using Specific Items

In third person view, when the player uses specific items, it temporarily switches to the first-person perspective. Upon stopping the use of that item, it returns to the third-person perspective.

Similar to the aim mode determination rules, [ItemPattern](./ItemPattern) are used for determining these types of items. Customization of related _ItemPattern_ can be done through configuration or resource packs.
