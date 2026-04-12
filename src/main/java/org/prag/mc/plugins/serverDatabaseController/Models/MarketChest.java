package org.prag.mc.plugins.serverDatabaseController.Models;

import jakarta.persistence.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.prag.mc.plugins.serverDatabaseController.Utils.ItemStackSerializer;

import java.sql.Timestamp;


@Entity
@Table(name = "market_chests", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_market_location",
                columnNames = {"world_name", "x", "y", "z"}
        )
})
public class MarketChest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_uuid", nullable = false)
    @Getter
    private RecordedPlayer owner;

    @Column(name = "world_name", nullable = false)
    private String worldName;

    private int x;
    private int y;
    private int z;

    @Column(name = "item_stack_data", columnDefinition = "TEXT", nullable = false)
    private String itemStackData;

    @Column(nullable = false)
    @Getter
    private double price;

    @Transient
    @Getter
    private int count;

    @Transient
    @Getter
    private boolean processing = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Getter
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "total_sales", nullable = false)
    @Getter
    private int totalSales = 0;

    protected MarketChest() {
    }


    // Chest block is the mere market container that is allowed, for now. As many containers are supported, new ones will be added.
    public MarketChest(RecordedPlayer owner, Chest chest, ItemStack itemToSell, double price) {
        this.owner = owner;
        this.worldName = chest.getLocation().getWorld().getName();
        this.x = chest.getLocation().getBlockX();
        this.y = chest.getLocation().getBlockY();
        this.z = chest.getLocation().getBlockZ();

        itemToSell = itemToSell.clone();
        itemToSell.setAmount(1);

        this.itemStackData = ItemStackSerializer.serialize(itemToSell);
        this.price = price;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName), (double) this.x, (double) this.y, (double) this.z);
    }

    public void incrementSales() {
        this.totalSales++;
    }

    public void incrementSales(int amount) {
        this.totalSales += amount;
    }


    public Block getContainer() {
        return getLocation().getBlock();
    }

    public void updateCount(int count) {

        if (count < 0)
            throw new IllegalArgumentException("Count cannot be negative!");


        this.count = count;
    }

    public void occupy() {
        this.processing = true;
    }

    public void release() {
        this.processing = false;
    }


    @Transient
    private ItemStack cachedItemStack;

    public ItemStack getItemStack() {

        if (cachedItemStack != null)
            return cachedItemStack;


        var result = ItemStackSerializer.deserialize(this.itemStackData);
        cachedItemStack = result;
        return result;


    }

    public void updateSignIfExist() {
        Block block = getContainer();

        if (!(block.getBlockData() instanceof org.bukkit.block.data.type.Chest chestData)) return;

        org.bukkit.block.BlockFace facing = chestData.getFacing();
        Block frontBlock = block.getRelative(facing);

        if (frontBlock.getState() instanceof org.bukkit.block.Sign sign) {
            org.bukkit.block.sign.SignSide side = sign.getSide(org.bukkit.block.sign.Side.FRONT);

            side.line(0, net.kyori.adventure.text.Component.text(owner.getNickname())
                    .color(net.kyori.adventure.text.format.NamedTextColor.GRAY)
                    .decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, true));

            String itemName = getItemStack().hasItemMeta() && getItemStack().getItemMeta().hasDisplayName()
                    ? net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(getItemStack().displayName())
                    : getItemStack().getType().name().replace("_", " ");

            side.line(1, net.kyori.adventure.text.Component.text(itemName)
                    .color(net.kyori.adventure.text.format.NamedTextColor.WHITE));

            side.line(2, net.kyori.adventure.text.Component.text("Price: " + price + " 'sec'")
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD));

            side.line(3, net.kyori.adventure.text.Component.text("Stock: " + count)
                    .color(net.kyori.adventure.text.format.NamedTextColor.YELLOW));

            sign.update(true, false);
        }
    }

}