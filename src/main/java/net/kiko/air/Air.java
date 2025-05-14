package net.kiko.air;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.server.MinecraftServer;
import java.util.HashMap;
import java.util.UUID;


//Comment
public class Air implements ModInitializer {
	public static final String MOD_ID = "kiko-air";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static class HomeState extends PersistentState {
		private final HashMap<UUID, BlockPos> homes = new HashMap<>();

		@Override
		public NbtCompound writeNbt(NbtCompound nbt) {
			NbtCompound homesTag = new NbtCompound();

			homes.forEach((uuid, pos) -> {
				NbtCompound posTag = new NbtCompound();
				posTag.putInt("x", pos.getX());
				posTag.putInt("y", pos.getY());
				posTag.putInt("z", pos.getZ());
				homesTag.put(uuid.toString(), posTag);
			});

			nbt.put("homes", homesTag);
			return nbt;
		}

		public static HomeState createFromNbt(NbtCompound tag) {
			HomeState state = new HomeState();
			NbtCompound homesTag = tag.getCompound("homes");

			for (String key : homesTag.getKeys()) {
				NbtCompound posTag = homesTag.getCompound(key);
				BlockPos pos = new BlockPos(
						posTag.getInt("x"),
						posTag.getInt("y"),
						posTag.getInt("z")
				);
				state.homes.put(UUID.fromString(key), pos);
			}

			return state;
		}

		public void setHome(UUID player, BlockPos pos) {
			homes.put(player, pos);
			markDirty();
		}

		public BlockPos getHome(UUID player) {
			return homes.get(player);
		}
	}

	private static HomeState homeState;

	private HomeState getHomeState(MinecraftServer server) {
		if (homeState == null) {
			PersistentStateManager persistentStateManager = server
					.getOverworld()
					.getPersistentStateManager();

			homeState = persistentStateManager.getOrCreate(
					HomeState::createFromNbt,
					HomeState::new,
					MOD_ID + "_homes"
			);
		}
		return homeState;
	}

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(net.minecraft.server.command.CommandManager.literal("sethome")
					.executes(context -> {
						var player = context.getSource().getPlayer();
						if (player != null) {
							BlockPos pos = player.getBlockPos();
							HomeState state = getHomeState(player.getServer());
							state.setHome(player.getUuid(), pos);
							player.sendMessage(Text.of("Home set to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
						}
						return 1;
					}));

			dispatcher.register(net.minecraft.server.command.CommandManager.literal("home")
					.executes(context -> {
						var player = context.getSource().getPlayer();
						if (player != null) {
							HomeState state = getHomeState(player.getServer());
							BlockPos pos = state.getHome(player.getUuid());
							if (pos != null) {
								player.requestTeleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
								player.sendMessage(Text.of("Teleported to home!"));
							} else {
								player.sendMessage(Text.of("No home set! Use /sethome first"));
							}
						}
						return 1;
					}));
		});
	}
}
