package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.advancement.Advancement;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PlayerAdvancementListener implements Listener {
    private final MineCordLink plugin;
    private final Map<String, String> advancementNames;

    public PlayerAdvancementListener(MineCordLink plugin) {
        this.plugin = plugin;
        this.advancementNames = initializeAdvancementNames();
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        String playerName = event.getPlayer().getName();
        Advancement advancement = event.getAdvancement();
        String advancementKey = advancement.getKey().getKey();
        String advancementName = getAdvancementName(advancementKey);

        if (advancementName != "") {
            String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setColor(Color.decode("F1C40F").getRGB())
                    .setAuthor(new WebhookEmbed.EmbedAuthor(playerName, avatarUrl, null))
                    .setDescription(playerName + " has made the advancement **" + advancementName + "**!")
                    .setTimestamp(Instant.now())
                    .build();

            plugin.getWebhookManager().sendMessage(
                    null, // No content, using embed instead
                    "Server",
                    null, // No avatar URL for webhook
                    embed);
        }
    }

    private String getAdvancementName(String key) {
        return advancementNames.getOrDefault(key, "");
    }

    private Map<String, String> initializeAdvancementNames() {
        Map<String, String> names = new HashMap<>();

        // Minecraft
        names.put("story/root", "Minecraft");
        names.put("story/mine_stone", "Stone Age");
        names.put("story/upgrade_tools", "Getting an Upgrade");
        names.put("story/smelt_iron", "Acquire Hardware");
        names.put("story/obtain_armor", "Suit Up");
        names.put("story/lava_bucket", "Hot Stuff");
        names.put("story/iron_tools", "Isn't It Iron Pick");
        names.put("story/deflect_arrow", "Not Today, Thank You");
        names.put("story/form_obsidian", "Ice Bucket Challenge");
        names.put("story/mine_diamond", "Diamonds!");
        names.put("story/enter_the_nether", "We Need to Go Deeper");
        names.put("story/shiny_gear", "Cover Me with Diamonds");
        names.put("story/enchant_item", "Enchanter");
        names.put("story/cure_zombie_villager", "Zombie Doctor");
        names.put("story/follow_ender_eye", "Eye Spy");
        names.put("story/enter_the_end", "The End?");

        // Nether
        names.put("nether/root", "Nether");
        names.put("nether/return_to_sender", "Return to Sender");
        names.put("nether/find_bastion", "Those Were the Days");
        names.put("nether/obtain_ancient_debris", "Hidden in the Depths");
        names.put("nether/fast_travel", "Subspace Bubble");
        names.put("nether/find_fortress", "A Terrible Fortress");
        names.put("nether/obtain_crying_obsidian", "Who is Cutting Onions?");
        names.put("nether/distract_piglin", "Oh Shiny");
        names.put("nether/ride_strider", "This Boat Has Legs");
        names.put("nether/uneasy_alliance", "Uneasy Alliance");
        names.put("nether/loot_bastion", "War Pigs");
        names.put("nether/use_lodestone", "Country Lode, Take Me Home");
        names.put("nether/netherite_armor", "Cover Me in Debris");
        names.put("nether/get_wither_skull", "Spooky Scary Skeleton");
        names.put("nether/obtain_blaze_rod", "Into Fire");
        names.put("nether/charge_respawn_anchor", "Not Quite \"Nine\" Lives");
        names.put("nether/ride_strider_in_overworld_lava", "Feels Like Home");
        names.put("nether/explore_nether", "Hot Tourist Destinations");
        names.put("nether/summon_wither", "Withering Heights");
        names.put("nether/brew_potion", "Local Brewery");
        names.put("nether/create_beacon", "Bring Home the Beacon");
        names.put("nether/all_potions", "A Furious Cocktail");
        names.put("nether/create_full_beacon", "Beaconator");
        names.put("nether/all_effects", "How Did We Get Here?");

        // The End
        names.put("end/root", "The End");
        names.put("end/kill_dragon", "Free the End");
        names.put("end/dragon_egg", "The Next Generation");
        names.put("end/enter_end_gateway", "Remote Getaway");
        names.put("end/respawn_dragon", "The End... Again...");
        names.put("end/dragon_breath", "You Need a Mint");
        names.put("end/find_end_city", "The City at the End of the Game");
        names.put("end/elytra", "Sky's the Limit");
        names.put("end/levitate", "Great View From Up Here");

        // Adventure
        names.put("adventure/root", "Adventure");
        names.put("adventure/voluntary_exile", "Voluntary Exile");
        names.put("adventure/spyglass_at_parrot", "Is It a Bird?");
        names.put("adventure/kill_a_mob", "Monster Hunter");
        names.put("adventure/read_power_of_chiseled_bookshelf", "The Power of Books");
        names.put("adventure/trade", "What a Deal!");
        names.put("adventure/trim_with_any_armor_pattern", "Crafting a New Look");
        names.put("adventure/honey_block_slide", "Sticky Situation");
        names.put("adventure/ol_betsy", "Ol' Betsy");
        names.put("adventure/lightning_rod_with_villager_no_fire", "Surge Protector");
        names.put("adventure/fall_from_world_height", "Caves & Cliffs");
        names.put("adventure/salvage_sherd", "Respecting the Remnants");
        names.put("adventure/avoid_vibration", "Sneak 100");
        names.put("adventure/sleep_in_bed", "Sweet Dreams");
        names.put("adventure/hero_of_the_village", "Hero of the Village");
        names.put("adventure/spyglass_at_ghast", "Is It a Balloon?");
        names.put("adventure/throw_trident", "A Throwaway Joke");
        names.put("adventure/kill_mob_near_sculk_catalyst", "It Spreads");
        names.put("adventure/shoot_arrow", "Take Aim");
        names.put("adventure/kill_all_mobs", "Monsters Hunted");
        names.put("adventure/totem_of_undying", "Postmortal");
        names.put("adventure/summon_iron_golem", "Hired Help");
        names.put("adventure/trade_at_world_height", "Star Trader");
        names.put("adventure/trim_with_all_exclusive_armor_patterns", "Smithing with Style");
        names.put("adventure/two_birds_one_arrow", "Two Birds, One Arrow");
        names.put("adventure/whos_the_pillager_now", "Who's the Pillager Now?");
        names.put("adventure/arbalistic", "Arbalistic");
        names.put("adventure/craft_decorated_pot_using_only_sherds", "Careful Restoration");
        names.put("adventure/adventuring_time", "Adventuring Time");
        names.put("adventure/play_jukebox_in_meadows", "Sound of Music");
        names.put("adventure/walk_on_powder_snow_with_leather_boots", "Light as a Rabbit");
        names.put("adventure/spyglass_at_dragon", "Is It a Plane?");
        names.put("adventure/very_very_frightening", "Very Very Frightening");
        names.put("adventure/sniper_duel", "Sniper Duel");
        names.put("adventure/bullseye", "Bullseye");

        // Husbandry
        names.put("husbandry/root", "Husbandry");
        names.put("husbandry/allay_deliver_item_to_player", "You've Got a Friend in Me");
        names.put("husbandry/froglights", "With Our Powers Combined!");
        names.put("husbandry/leash_all_frog_variants", "When the Squad Hops into Town");
        names.put("husbandry/ride_a_boat_with_a_goat", "Whatever Floats Your Goat!");
        names.put("husbandry/wax_on", "Wax On");
        names.put("husbandry/wax_off", "Wax Off");
        names.put("husbandry/bred_all_animals", "Two by Two");
        names.put("husbandry/silk_touch_nest", "Total Beelocation");
        names.put("husbandry/breed_an_animal", "The Parrots and the Bats");
        names.put("husbandry/kill_axolotl_target", "The Healing Power of Friendship!");
        names.put("husbandry/axolotl_in_a_bucket", "The Cutest Predator");
        names.put("husbandry/tactical_fishing", "Tactical Fishing");
        names.put("husbandry/obtain_sniffer_egg", "Smells Interesting");
        names.put("husbandry/obtain_netherite_hoe", "Serious Dedication");
        names.put("husbandry/plant_any_sniffer_seed", "Planting the Past");
        names.put("husbandry/feed_snifflet", "Little Sniffs");
        names.put("husbandry/make_a_sign_glow", "Glow and Behold!");
        names.put("husbandry/fishy_business", "Fishy Business");
        names.put("husbandry/tadpole_in_a_bucket", "Bukkit Bukkit");
        names.put("husbandry/allay_deliver_cake_to_note_block", "Birthday Song");
        names.put("husbandry/tame_an_animal", "Best Friends Forever");
        names.put("husbandry/safely_harvest_honey", "Bee Our Guest");
        names.put("husbandry/plant_seed", "A Seedy Place");
        names.put("husbandry/complete_catalogue", "A Complete Catalogue");
        names.put("husbandry/balanced_diet", "A Balanced Diet");

        return names;
    }
}