# RouteThree
RouteThree is a Pokémon game speedrun routing tool designed for Generation 3 games (Ruby/Sapphire/Emerald).  
It is derived from Dabomstew & entrpntr's RouteTwo, itself derived from HRoll's RouteOne.

### TABLE OF CONTENTS
1. [LATEST CHANGES](#1-latest-changes)
2. [INSTALLATION](#2-installation)
3. [CONFIGURATION FILES](#3-CONFIGURATION-FILES)
	1. [Master file](#31-Master-file)
	2. [Configuration files](#32-Configuration-files)
4. [PRELIMINARY NOTES FOR ROUTE FILES](#4-PRELIMINARY-NOTES-FOR-ROUTE-FILES)
5. [MAKING A ROUTE FILE](#5-making-a-route-file)
   1. [Comments](#51-comments)
   2. [Generic output](#52-generic-output)
      1. [Money](#521-money)
	  2. [Stats](#522-stats)
   3. [Player Pokemon utility](#53-player-pokemon-utility)
      1. [Player Pokemon](#531-player-pokemon)
	  2. [Items](#532-items)
	  3. [Player money](#533-player-money)
	  4. [Badges](#534-badges)
   4. [Battles](#54-battles)
      1. [Trainers](#541-trainers)
	  2. [Wild encounters](#542-wild-encounters)
	  3. [Battle options](#543-battle-options)
		 1. [Stat boosts](#5431-stat-boosts)
		 2. [Experience](#5432-experience)
		 3. [Weather](#5433-weather)
		 4. [Status](#5434-status)
		 5. [Various modifiers](#5435-Various-modifiers)
		 6. [Order](#5436-order)
		 7. [Double battle](#5437-double-battle)
		 8. [Output](#5438-output)
6. [KNOWN ISSUES](#6-known-issues)
7. [TODOS](#7-todos)
8. [CONTACT INFO AND ACKNOWLEDGEMENTS](#8-contact-info-and-acknowledgements)
   1. [Communities](#81-communities)
   2. [Coders](#82-coders)


### 1. LATEST CHANGES
*Syntax :*    
**`[YYYY/MM/DD]` - version**  
► Compatibility-breaking update.  
○ Usual update.


**`????????????` - v0.3**  
○ Added FireRed/LeafGreen support, and Fire Red Squirtle High Exp route file.  
○ Added Emerald abraful route file.  
○ Added battle option `-doublebattle` to force a double battle. Updated the .xml file accordingly.  
○ Added most residual damages : poisoned, badly poisoned, burned, trapped, confused, seeded, nightmared and cursed.  
○ Fixed a bug involving Special Defense of opponents not being modified properly.  

**`[2022/01/13]` - v0.2.1**  
○ Refactoring of the damage calculation and printing.  
○ Implementation of Psywave, Flail, Rage, Rollout, Fury Cutter, Magnitude, Low Kick and Future Sight.  

**`[2022/01/05]` - v0.2**  
○ Added Emerald support (and trainer data).  
○ Added Notepad++ .xml formatting file for route files.

**`[2022/01/02]` - v0.1**  
○ Initial beta release.
-- -- 
### 2. INSTALLATION
The tool as been tested and is proven to work with Java 1.7 .

#### 2.1 From the latest release
[Download the latest release from this page.](https://github.com/UnderscorePoY/RouteThree/releases)  
Run the .jar file.

#### 2.2 From source files
Download source files, and build the executable jar with Java 1.7 .  
IntelliJ IDEA is known to NOT work, as the ini4j package doesn't seem to be compatible with this IDE.

### 3. CONFIGURATION FILES
All tag-value fields are of the following format : `tag = value`.  
When values are filenames, they are case sensitive. Otherwise, they are case insensitive.  
Filenames can traverse folders. For instance, `test.txt`, `routes/route1.txt` or `../../configs/config.ini` are valid filenames.

#### 3.1. Master file
The `master.ini` file is the entry point of the program. It allows you to choose which configuration file will be loaded by the tool.
##### Section : `[master]`
  Tag | Expected value | Usage
  --------------- | -------------- | --------------
  `"configFile"`   | A filename.  | The configuration file to be loaded.
 `"debugFile"` | A filename. | The debugging file in case of loading issues.
 
#### 3.2. Configuration files
A configuration file (generally with the `.ini` extension) gathers the primary information used in the route.

##### Section : `[game]`
   Tag | Expected value | Usage
  --------------- | -------------- | --------------
  `"game"`   | `ruby`, `sapphire`, `emerald`, `firered` or `leafgreen`.| The name of the game.
  
##### Section : `[poke]`
   Tag | Expected value | Usage
  --------------- | -------------- | --------------
  `"species"`   | A string. | The name of the main Pokémon species.
  `"level"`   | An integer between `1` and `100`.| The starting level of the species.
  `"nature"`   | A string. | The nature of the species. <br/>(Defaults to a neutral species if missing.)
  `"ability"`   | A string. | The ability of the species. (Defaults to the first species ability if missing.)
  `"hpIV"`   | An integer between `0` and `31`.| The Hit Point Individual Value (IV) of the main Pokémon. <br/>(Defaults to `31` if missing.)
  `"atkIV"`   | An integer between `0` and `31`.| The Hit Point Individual Value (IV) of the main Pokémon. <br/>(Defaults to `31` if missing.)
  `"defIV"`   | An integer between `0` and `31`.| The Defense Individual Value (IV) of the main Pokémon. <br/>(Defaults to `31` if missing.)
  `"spaIV"`   | An integer between `0` and `31`.| The Special Attack Individual Value (IV) of the main Pokémon. <br/>(Defaults to `31` if missing.) 
  `"spdIV"`   | An integer between `0` and `31`.| The Special Defense Individual Value (IV) of the main Pokémon. <br/>(Defaults to `31` if missing.)
  `"speIV"`   | An integer between `0` and `31`.| The Speed Individual Value (IV) of the main Pokémon. <br/>(Defaults to `31` if missing.)
  `"boostedExp"`   | `true` or `false`. | Whether the main Pokémon benefits from the trading experience boost or not. <br/>(Defaults to `false` if missing.)
  `"pokerus"`   | `true` or `false`. | Whether the main Pokémon benefits from the Pokérus Effort Value (EV) boost or not. <br/>(Defaults to `false` if missing.)
  
##### Section : `[files]`
   Tag | Expected value | Usage
  --------------- | -------------- | --------------
  `"routeFile"`   | A filename. | The name of the route file the tool will read from.
  `"outputFile"`   | A filename. | The name of the output file the tool will write to.

##### Section : `[util]`
   Tag | Expected value | Usage
  --------------- | -------------- | --------------
  `"overallChanceKO"`   | `true` or `false`. | Whether the tool displays overall chance of KOing the opponent or not. <br/>(Defaults to `false` if missing.)
  `"showGuarantees"`   | `true` or `false`. | Whether the information that n-shots are guaranteed is displayed or not. <br/>(Defaults to `false` if missing.)
  `"printxitems"`   | `true` or `false`. | Whether the number of X Items used is displayed at the end of the file or not. <br/>(Defaults to `false` if missing.)
  `"printrarecandies"`   | `true` or `false`. | Whether the number of Rare Candies used is displayed at the end of the file or not. <br/>(Defaults to `false` if missing.)
  `"printstatboosters"`   | `true` or `false`. | Whether the number of Vitamins used is displayed at the end of the file or not. <br/>(Defaults to `false` if missing.)
  
-- -- 

### 4. PRELIMINARY NOTES FOR ROUTE FILES

`"command"` : Every command will be put between quotation marks. You SHOULD NOT be writing these quotation marks in your routing files.  
`"alias"` : An alias refers to a shorter name for a given command.  
`"CoMMAnd"` : Every command is case-incensitive. You can capitalize at will.  
○ The convention from the initial release is to keep commands lowercase, and names/moves/etc uppercase.  
○ You can disobey this convention at will.  
○ For readability purposes, the commands here will display some uppercase letters.  
`<ARGUMENT>` : When using angle brackets, it refers to a mandatory argument. You SHOULD NOT be writing these brackets in your routing files.  
`[ARGUMENT]` : when using square brackets, it refers to an optional argument. You can omit it if you don't need it. You SHOULD NOT be writing these brackets in your routing files. 

The `resources` folder contains most of the data the tool loads. You can look into the different files to check on trainer names, item names, etc.  

-- -- 

### 5. MAKING A ROUTE FILE

#### 5.1. COMMENTS
  `"//"` : Starts a comment. Either at the start of a line or at the end of instructions. Used as documentation for the reader/router.  

#### 5.2. GENERIC OUTPUT

##### 5.2.1. Money
  `"money"` : Displays the current Player money.  
  
##### 5.2.2. Stats
  `"ranges"` : Displays a table of the main Pokemon stats for all DV values.  
  `"stats"`  : Displays the main Pokemon stats as they are in the Pokemon menu.  
  
  *Stats options :*  
  `"-b"` : Add to account for stat badge boosts.  

#### 5.3. PLAYER POKEMON UTILITY

##### 5.3.1. Player Pokemon
  `"evolve <SPECIES>"` : Changes your Pokemon to `SPECIES`.  
  alias: `"e"`           
> Example : `evolve COMBUSKEN // I'm Ryziken`  
  
  `"learnMove <MOVE>"` : Learns move `MOVE`. `MOVE` is written with only letters (no spaces, no dashes, no underscores, etc.)  
  alias: `"lm"`          
> Example : `learnmove HIDDENPOWER // "Any Pokémon is runnable with a proper Hidden Power" - Nobody, ever`  
  
  `"unlearnMove <MOVE>"` : Unlearns move `MOVE`. `MOVE` is written with only letters (no spaces, no dashes, no underscores, etc.)  
  alias: `"um"`            
> Example : `unlearnmove GROWL // Useless move`  
  
  `"rareCandy"` : Uses a Rare Candy on your Pokemon.  
  alias: `"rc"`  
  
  `"hpup"`  : Uses an HP Up on your Pokemon.  
  `"protein"` : Uses a Protein on your Pokemon.  
  `"iron"` : Uses an Iron on your Pokemon.  
  `"calcium"` : Uses a Calcium on your Pokemon.  
  `"zinc"` : Uses a Zinc on your Pokemon.  
  `"carbos"`  : Uses a Carbos on your Pokemon.  

`"pokerus"` : Infects your Pokémon with Pokérus. Allows to double EV yields until maximum values are reached.

`"setBoostedExp"` : Activates the x1.5 multiplier from traded Pokémon. (Should be useless, as it's an available setting in the config file.)  
`"unsetBoostedExp"` : Deactivates the previous multiplier.

##### 5.3.2. Items
`"equip <ITEM>"` : Equips the item `ITEM`. If an item was already held, it is replaced by the specified one.
> Example : `equip SOFTSAND // Mud Slap go brrrr`

  `"unequip"` : Unequips the held item.
  
  Here is an exhaustive list of items which effects are implemented :
##### Money
  Item name|  Multiplier  
  --------------- | -------------- 
  `"amuletCoin"`   | x2  

##### Experience
  Item name|  Multiplier  
  --------------- | -------------- 
  `"luckyEgg"`   | x2  

##### Species-boosting items
  Item name| Species        | Boosted stats             | Multiplier  
  --------------- | -------------- | ------------------------- | ----------
  `"lightBall"`   | Pikachu        | Special Attack            | x2  
  `"metalPowder"` | Ditto          | Defense | x2  
  `"thickClub"`   | Cubone/Marowak | Attack                    | x2  
  `"soulDew"`   | Latios/Latias | Special Attack & <br/> Special Defense | x1.5 <br/>(outside Battle Tower) 
  `"deepSeaTooth"`   | Clamperl | Special Attack | x2  
  `"deepSeaScale"`   | Clamperl | Special Defense | x2  

##### Type-boosting items  
  Item name| Boosted type 
  ---------------- | -------
  `"blackBelt"`    | Fighting  
  `"blackGlasses"` | Dark  
  `"charcoal"`     | Fire  
  `"dragonScale"`  | Dragon
  `"hardStone"`    | Rock  
  `"magnet"`       | Electric  
  `"metalcoat"`    | Steel  
  `"miracleSeed"`  | Grass  
  `"mysticWater"`  | Water  
  `"neverMeltIce"` | Ice  
  `"pinkBow"`      | Normal  
  `"poisonBarb"`   | Poison  
  `"polkadotBow"`  | Normal  
  `"sharpBeak"`    | Flying  
  `"silkScarf"` | Normal
  `"silverPowder"` | Bug  
  `"softSand"`     | Ground  
  `"spellTag"`     | Ghost  
  `"twistedSpoon"` | Psychic  

##### 5.3.3. Player money
  These commands only affect money, since there is no inventory management.  
  `"buy [QUANTITY] <ITEM>"`  : Buys `ITEM` `QUANTITY` times. `ITEM` only in letters.  If `QUANTITY` is omitted, it defaults to `1`.  
  `"sell [QUANTITY] <ITEM>"` : Sells `ITEM` `QUANTITY` times.  
> Example : `buy 46 XATTACK // Zigzagoon, go !`  
> Example : `sell HPUP // Sells 1 HPUP`
  
  `"addMoney <NUM>"` : Adds `NUM` to player's money.  
> Example : `addmoney 5000 // Long live the casino !`  

  `"spendMoney <NUM>"` : Spends `NUM` money.  
> Example : `spendmoney 50 // Museum entrance fee ...`  
      
##### 5.3.4. Badges  
Defeating `Roxanne`, `Wattson`, `Norman` or `TateAndLiza` automatically activates their respective badge boost.

The following commands give you the desired badge without fighting its corresponding Gym Leader.  
  This is useful when you route Pokémon you don't acquire/catch straight away.  
  Commands         | Gym Leader | Boosted stats
  ---------------- | ---------- | ---------- 
  `"stonebadge"`  | `Roxanne` | Attack     
  `"dynamobadge"`    | `Wattson` | Speed         
  `"balancebadge"`   | `Norman` | Defense 
  `"mindbadge"`     | `TateAndLiza` | Special Attack & Special Defense       
  
#### 5.4. BATTLES
##### 5.4.1. Trainers
  `"<NAME>"`  : Triggers a trainer battle against the trainer with name `NAME`.  
  
##### 5.4.2. Wild encounters
  `"L<NUM> <SPECIES> <NATURE>"` : Triggers a wild battle against a level `NUM` `SPECIES` with nature `NATURE` and perfect IVs.  
  
  *Wild encounters options :*  
  `"<ATK> <DEF> <SPA> <SPD> <SPE>"` : Gives IVs `ATK DEF SPA SPD SPE` to the wild encounter.  
> Example : `L45 KYOGRE HASTY 27 31 7 25 18 26 // Fast Boi`  

  `"-trainer"` : Sets the wild encounter as a trainer Pokemon. Mainly gives access to the x1.5 experience multiplier.  
  alias: `"-t"`  
> Example : `L31 MILOTIC GENTLE -trainer // I don't remember this trainer name`
  
  `"-wild"`     : Sets the wild encounter as wild. A wild encounter is wild by default, this option can be omitted.  
  alias: `"-w"`  
      
##### 5.4.3. Battle options
  For all battle options, `x` refers to the player, `y` refers to the enemy.  
  Any option starting with `-x` can be written starting with `-y` to have the same effect on the enemy team.  
  
###### 5.4.3.1. Stat boosts
  `"-xitems <ATK>/<DEF>/<SPA>/<SPD>/<SPE>[/ACC][/EVA]"` : Sets `ATK` X Attacks, `DEF` X Defends, etc.  
  alias: `"-x"`  
> Example : `NORMAN -xitems 4/3/0/0/3/1 // Sets up 4 X Attacks, 3 X Defends, 3 X Speeds & 1 X Accuracy for the player for the whole battle`  


All the following commands apply for the entire duration of the battle.  
`NUM` should be an integer between `-6` and `+6`. Positive numbers can omit the `+` sign.  
  `"-xatk <NUM>"`   : Sets up `NUM` X Attacks.  
  `"-xdef <NUM>"`   : Sets up `NUM` X Defends.  
  `"-xspa <NUM>"`   : Sets up `NUM` X Specials. Only applies to the Special Attack stat.  
  `"-xspd <NUM>"` : Boosts Special Defense `NUM` times. Useful for Amnesia, etc.   
  `"-xspe <NUM>"` : Sets up `NUM` X Speeds.  
  `"-xacc <NUM"`         : Sets up `NUM` X Accuracies.  
  `"-xeva <NUM"`         : Boosts Evasion`NUM` times.  
> Example : `LANCE -xspd 1 -xspc 2 // Sets 1 X Speed & 2 X Specials forthe entire fight`  
  
  `"-xatks <FIRST>[/SECOND...]"`   : Sets up `FIRST` X Attacks for the 1st Pokemon, `SECOND` X Attacks for the 2nd, etc.  
  `"-xdefs <FIRST>[/SECOND...]"`   : Sets up `FIRST` X Defends for the 1st Pokemon, `SECOND` X Defends for the 2nd, etc.  
  `"-xspas <FIRST>[/SECOND...]"` : Sets up `FIRST` X Specials for the 1st Pokemon, `SECOND` X Specials for the 2nd, etc. Only applies to Special Attack.              
  `"-xspds <FIRST>[/SECOND...]"` : Applies `FIRST` Special Defense boosts for the 1st Pokemon, `SECOND` Special Defense boosts for the 2nd, etc.  
    `"-xspes <FIRST>[/SECOND...]"`   : Sets `FIRST` X Speeds for the 1st Pokemon, `SECOND` X Speeds for the 2nd, etc.  
 `"-xaccs <FIRST>[/SECOND...]"`   : Sets `FIRST` X Accuracies for the 1st Pokemon, `SECOND` X Accuracies for the 2nd, etc.  
`"-xevas <FIRST>[/SECOND...]"`   : Applies `FIRST`  Evasions boosts for the 1st Pokemon, `SECOND` Evasions boosts for the 2nd, etc.  
> Example : `GLACIA -xspas 0/0/2/2/2 // Sets up 2 X Specials on her third Pokemon`  

###### 5.4.3.2. Experience
  `"-sxp <NUM>"` : Divides all earned experience by `NUM`.  
  `"-sxps <FIRST>[/SECOND...]"` : Divides first enemy Pokemon experience by `FIRST`, the second by `SECOND`, etc.  
> Example : `TATEANDLIZA -sxps 2/1 // First Pokémon is KOed with an ally on the field, but the second one is defeated alone`  

###### 5.4.3.3. Weather
  `"-weather <WEATHER>"` : Sets the weather for the entire battle.  
  `"-weathers <FIRST>[/SECOND...]` Sets the weather `FIRST` for the first enemy Pokémon, weather `SECOND` for the second one, etc.
> Arguments : within `NONE`/`RAIN`/`SUN`/`SANDSTORM/HAIL` (`NONE` can be replaced by `0`).  
> Example : `FLANNERY -weather SUN // Sunny Day !`  

###### 5.4.3.4. Status
`"-xstatus <STATUS>"` : Sets the "desired" status. 
> Arguments : within `SLEEP`/`POISON`/`BURN`/`FREEZE`/`PARALYSIS`/`TOXIC`.  
> Example : `BRAWLY -xstatus BURN // Gust all the way !`

###### 5.4.3.5. Various modifiers
`"-xstatus2 <MOD1>[/MOD2...]"` : Sets a list of battle modifiers. 
> Example : `TATEANDLIZA -ystatus2 REFLECT/LIGHTSCREEN // The enemy side sets up both screens`

Here is the exhautive list of implemented modifiers :
 Modifier | Damage multiplier
  ---------------- | ---------- 
  `"CHARGED_UP"`  | x2 for Electric type moves.   
  `"MUDSPORT"`  | x1/2 for Electric type moves.   
  `"WATERSPOUT"`  | x1/2 for Fire type moves.   
  `"UNDERWATER"`  | x2 if using Surf.   
  `"REFLECT"`  | x1/2 for Physical moves (x2/3 during double battles).   
  `"LIGHTSCREEN"`  | x1/2 for Special moves (x2/3 during double battles).   
  
`-xtorrent`, `-xblaze`, `-xovergrow`, `-xswarm` : Activates the corresponding abilities.  

###### 5.4.3.6. Order
  `"-order <FIRST>[/SECOND...]"` : Switches the enemy team order.
> Note : Useful for good AI trainers who send stronger Pokemon first.  
> Example : `"WATTSON -order 1/3/2 // Sends 1st Pokémon, then the 3rd, then the 2nd`  

###### 5.4.3.7. Double battle
  `"-doubleBattle"` : Forces the battle to be a double battle. Useful for move damage calculation only.  
  alias: `"-double"`

###### 5.4.3.8. Output
  `"-lvstats"` : Outputs player Pokemon stats when a level up occurs during a battle.  
  `"-lvranges"`  : Outputs player Pokemon ranges when a level up occurs during a battle.  

  `"-verbose <LEVEL>"` : Activates output for the desired battle.  
  alias: `"-v"`          
> Argument : within NONE/SOME/ALL/EVERYTHING or 0/1/2/3.  
> Note : `-v 0` is equivalent to skipping output for the battle.  

-- -- 	

### 6. KNOWN ISSUES
○ Probably a lot.

-- -- 

### 7. TODOS

○ Much more than this page can ever contain.

-- -- 

### 8. CONTACT INFO AND ACKNOWLEDGEMENTS

#### 8.1. COMMUNITIES
[`PokemonSpeedruns` Discord server (archived)](https://discord.gg/0UUw8zDe2hWlwRsm)  

[`Gen 1-3 Pokemon Speedrunning` Discord server](https://discord.gg/NjQFEkc)  

#### 8.2. CODERS
[`HRoll`](https://github.com/HRoll) [(2)](http://twitch.tv/hroll) - for making the original RouteOne which contributes 80% of the code for this  
 
[`Mountebank`](http://twitch.tv/mountebank) - for contributing to the development of the original RouteOne  

[`SpeedRunsLive`](http://speedrunslive.com) - for inspiration/awesome races  

[`Dabomstew`](https://github.com/Dabomstew) - for porting RouteOne to fit Gen 2  

[`entrpntr`](https://github.com/entrpntr) - for the attention to details and the various ideas  


-- -- 
