package io.github.wamel04.crafters_quest.npc.angle_updater;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.wamel04.crafters_quest.CraftersQuestPlugin;
import org.bukkit.Bukkit;
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

    Float currentYaw;
    Float currentPitch;

    float targetYaw;
    float targetPitch;

    public PersonalAngleUpdater(Entity entity, Entity target, Player forPlayer, double sensitivity, boolean isSelf) {
        this.entity = entity;
        this.target = target;
        this.forPlayer = forPlayer;
        this.initSensitivity = sensitivity;
        this.sensitivity = sensitivity;
        this.isSelf = isSelf;

        if (entity instanceof Player)
            Bukkit.getScheduler().runTask(CraftersQuestPlugin.getInstance(), () -> ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, 3, false, false)));

        calculate();
    }

    private void calculate() {
        double dx = target.getLocation().getX() - entity.getLocation().getX();
        double dy = target.getLocation().getY() - entity.getLocation().getY();
        double dz = target.getLocation().getZ() - entity.getLocation().getZ();

        targetYaw = (float) (Math.toDegrees(Math.atan2(dx, dz)) * -1);
        targetPitch = (float) (Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))) * -1);

        if (isSelf || currentYaw == null) {
            currentYaw = entity.getYaw();
            currentPitch = entity.getPitch();
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
        if (Math.abs(targetYaw - currentYaw) <= AngleUpdater.MAX_DISTANCE && Math.abs(targetPitch - currentPitch) <= AngleUpdater.MAX_DISTANCE) {
            sensitivity = initSensitivity;

            calculate();
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

        if (isSelf) {
            PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.POSITION);
            teleportPacket.getIntegers().write(0, 0);
            teleportPacket.getModifier().write(3, changeYaw);
            teleportPacket.getModifier().write(4, changePitch);
            ProtocolLibrary.getProtocolManager().sendServerPacket(forPlayer, teleportPacket);
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

        sensitivity *= 1.1;
    }
}
