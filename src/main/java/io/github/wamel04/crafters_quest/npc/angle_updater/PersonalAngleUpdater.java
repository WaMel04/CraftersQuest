package io.github.wamel04.crafters_quest.npc.angle_updater;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PersonalAngleUpdater {

    private Entity entity;
    private Entity target;
    private Player forPlayer;
    private final double initSensitivity;
    private double sensitivity;
    private boolean isSelf;

    private Float currentYaw;
    private Float currentPitch;

    private float targetYaw;
    private float targetPitch;

    private boolean isDone = false;

    public PersonalAngleUpdater(Entity entity, Entity target, Player forPlayer, double sensitivity, boolean isSelf) {
        this.entity = entity;
        this.target = target;
        this.forPlayer = forPlayer;
        this.initSensitivity = sensitivity;
        this.sensitivity = sensitivity;
        this.isSelf = isSelf;

        if (entity instanceof Player)
            Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.getByKey(NamespacedKey.minecraft("slowness")), -1, 3, false, false)));

        calculate();
    }

    private void calculate() {
        double dx = target.getLocation().getX() - entity.getLocation().getX();
        double dy = target.getLocation().getY() - entity.getLocation().getY();
        double dz = target.getLocation().getZ() - entity.getLocation().getZ();

        targetYaw = (float) (Math.toDegrees(Math.atan2(dx, dz)) * -1);
        targetPitch = (float) (Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))) * -1);

        if (isSelf || currentYaw == null) {
            currentYaw = entity.getLocation().getYaw();
            currentPitch = entity.getLocation().getPitch();
        }

        if (targetYaw - currentYaw > 180)
            currentYaw += 360;
        else if (currentYaw - targetYaw > 180)
            currentYaw -= 360;
        if (targetPitch - currentPitch > 180)
            currentPitch += 360;
        else if (currentPitch - targetPitch > 180)
            currentPitch -= 360;
    }

    public void updateAngle() {
        if (isDone) {
            calculate();

            if (Math.abs(targetYaw - currentYaw) >= AngleUpdater.MAX_YAW_DIFFERENCE || Math.abs(targetPitch - currentPitch) >= AngleUpdater.MAX_PITCH_DIFFERENCE) {
                isDone = false;
                sensitivity = initSensitivity;
            }

            return;
        }

        float changeYaw = (float) (Math.abs(targetYaw - currentYaw) * sensitivity);
        float changePitch = (float) (Math.abs(targetPitch - currentPitch) * sensitivity);

        if (currentYaw > targetYaw)
            changeYaw *= -1;

        currentYaw += changeYaw;

        if (currentPitch > targetPitch)
            changePitch *= -1;

        currentPitch += changePitch;

        if (Math.abs(targetYaw - currentYaw) <= AngleUpdater.MIN_DIFFERENCE && Math.abs(targetPitch - currentPitch) <= AngleUpdater.MIN_DIFFERENCE) {
            if (isSelf)
                isDone = true;

            sensitivity = initSensitivity;

            calculate();
            return;
        }
        if (isSelf) {
            if (Bukkit.getBukkitVersion().contains("1.21")) {
                PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_ROTATION);

                // 1.21.4, protocolLib 5.4.0
                teleportPacket.getFloat().write(0, currentYaw);
                teleportPacket.getFloat().write(1, currentPitch);
                ProtocolLibrary.getProtocolManager().sendServerPacket(forPlayer, teleportPacket);
            } else {
                PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.POSITION);
                teleportPacket.getIntegers().write(0, Integer.valueOf(0));
                teleportPacket.getDoubles().write(0, Double.valueOf(forPlayer.getLocation().getX()));
                teleportPacket.getDoubles().write(1, Double.valueOf(forPlayer.getLocation().getY()));
                teleportPacket.getDoubles().write(2, Double.valueOf(forPlayer.getLocation().getZ()));
                teleportPacket.getFloat().write(0, currentYaw);
                teleportPacket.getFloat().write(1, currentPitch);
                ProtocolLibrary.getProtocolManager().sendServerPacket(forPlayer, teleportPacket);
            }
        } else {
            PacketContainer bodyPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
            bodyPacket.getIntegers().write(0, entity.getEntityId());
            bodyPacket.getBytes().write(0, (byte) ((currentYaw / 360.0F) * 256F));
            bodyPacket.getBytes().write(1, (byte) ((currentPitch / 360.0F) * 256F));
            ProtocolLibrary.getProtocolManager().sendServerPacket(forPlayer, bodyPacket);

            PacketContainer headPacket = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            headPacket.getIntegers().write(0, entity.getEntityId());
            headPacket.getBytes().write(0, (byte) ((currentYaw / 360.0F) * 256F));
            ProtocolLibrary.getProtocolManager().sendServerPacket(forPlayer, headPacket);
        }

        sensitivity *= 1.01;
    }

    public boolean isDone() {
        return isDone;
    }

}
