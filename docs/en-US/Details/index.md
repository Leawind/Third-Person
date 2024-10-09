# 📖 Detailed

## Item-predicates

In third-person view, the mod will determine whether to enter aim mode based on the item held by the player and using state.

Conventions:

-   **Item-predicate** refers to `net.minecraft.advancements.criteria.ItemPredicate` object, which represents a kind of rules that can determine if an ItemStack matches its criteria.
-   **[Item-pattern](./ItemPattern)** is a human-edited string that can be parsed into an ItemPredicate.  
    Format: `[#][namespace:]<id>[nbt]` or `<nbt>`

During gameplay, this mod maintains 3 sets of item-predicates:

-   **Aim While Holding**: When any item the player is holding matches any item predicate in this set, aim mode is activated.
-   **Aim While Using**: When the item the player is using matches any item-predicate in this set, aim mode is activated.
-   **Temporarily Enter First-Person While Using**: When the item the player is using matches any item-predicate in this set, the view temporarily switches to first-person.

The item-predicates in these sets come from 2 sources:

-   **Mod Configuration**: Item-patterns can be added in the mod's configuration file or through the configuration interface in game, and the mod will automatically parse them into item-predicates.
-   **[Resource Packs](./ResourcePack.md)**: This includes resource packs in this mod, those in other mods, and any additional resource packs manually added by the player.
