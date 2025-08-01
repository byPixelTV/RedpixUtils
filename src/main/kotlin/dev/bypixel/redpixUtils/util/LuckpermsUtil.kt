package dev.bypixel.redpixUtils.util

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.PrefixNode
import org.bukkit.entity.Player
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.CompletableFuture

object LuckpermsUtil {
    val luckPerms: LuckPerms = LuckPermsProvider.get()

    fun getSortedGroupsByWeight(): List<Group> {
        // Fetch all groups from LuckPerms
        val groups = luckPerms.groupManager.loadedGroups

        // Sort groups by weight in descending order
        return groups.sortedByDescending { group ->
            group.weight.orElse(0) // Default weight if not set
        }
    }

    fun getAllGroupWeights(): List<Int> {
        return getSortedGroupsByWeight().map { it.weight.orElse(0) }
    }

    fun getSortedGroupNamesByWeight(): List<String> {
        // Use the first function to get the groups sorted by weight
        val sortedGroups = getSortedGroupsByWeight()

        // Map each group to its name
        return sortedGroups.map { group -> group.name }
    }

    fun returnTabPrefix(player: Player) : String {
        val prefix = getLuckPermsPrefix(player)
        return TextUtil.convertMiniMessageToLegacy(prefix ?: "")
    }

    fun returnTabSuffix(player: Player) : String {
        val suffix = getSuffix(player)

        return TextUtil.convertMiniMessageToLegacy(suffix ?: "")
    }

    fun getLuckPermsPrefix(player: Player): String? {
        val user = LuckPermsProvider.get().userManager.getUser(player.uniqueId)
        val prefix = user?.cachedData?.metaData?.prefix
        return prefix
    }

    fun getPrefixOfUuidSync(uuid: UUID): String? {
        val user = luckPerms.userManager.getUser(uuid)
        return user?.cachedData?.metaData?.prefix
    }

    fun getPrefixOfUuidAsync(uuid: UUID): CompletableFuture<String?> {
        return luckPerms.userManager.loadUser(uuid).thenApply { user ->
            user?.cachedData?.metaData?.prefix
        }
    }

    fun getGroup(player: Player): String? {
        val user = LuckPermsProvider.get().userManager.getUser(player.uniqueId)
        return user?.primaryGroup
    }

    fun getSuffix(player: Player): String? {
        val user = LuckPermsProvider.get().userManager.getUser(player.uniqueId)
        val suffix = user?.cachedData?.metaData?.suffix
        return suffix
    }

    fun getGroupWeightOfUUID(uuid: UUID, callback: (Int) -> Unit) {
        val userFuture: CompletableFuture<User> = luckPerms.userManager.loadUser(uuid)
        userFuture.thenAcceptAsync { user ->
            val weight = LuckPermsProvider.get().groupManager.getGroup(user.primaryGroup)?.weight?.orElse(0) ?: 0
            callback(weight)
        }
    }

    fun getGroupOfUUID(uuid: UUID, callback: (String?) -> Unit) {
        val userFuture: CompletableFuture<User> = luckPerms.userManager.loadUser(uuid)
        userFuture.thenAcceptAsync { user ->
            val group = user.primaryGroup
            callback(group)
        }
    }

    fun getGroupOfUUIDSync(uuid: UUID): String? {
        val user = luckPerms.userManager.getUser(uuid)
        return user?.primaryGroup
    }

    fun getPermissionOfUUID(uuid: UUID, permission: String, callback: (Boolean?) -> Unit) {
        val userFuture: CompletableFuture<User> = luckPerms.userManager.loadUser(uuid)
        userFuture.thenAcceptAsync { user ->
            callback(user.cachedData.permissionData.checkPermission(permission).asBoolean())
        }
    }


    fun getPrefixOfUUID(uuid: UUID, callback: (String?) -> Unit) {
        val userFuture: CompletableFuture<User> = luckPerms.userManager.loadUser(uuid)
        userFuture.thenAcceptAsync { user ->
            val prefix = user.cachedData.metaData.prefix
            callback(prefix)
        }
    }

    fun getSuffixOfUUID(uuid: UUID, callback: (String?) -> Unit) {
        val userFuture: CompletableFuture<User> = luckPerms.userManager.loadUser(uuid)
        userFuture.thenAcceptAsync { user ->
            val suffix = user.cachedData.metaData.suffix
            callback(suffix)
        }
    }

    fun setPrefixMetaOfOfflinePlayer(uuid: UUID, prefix: String) {
        val user = luckPerms.userManager.getUser(uuid)

        if (user != null) {
            val prefixNode = PrefixNode.builder(prefix, 888).build()
            user.data().add(prefixNode)

            luckPerms.userManager.saveUser(user)
        }

    }

    fun removePrefixMetaOfOfflinePlayer(uuid: UUID) {
        val user = luckPerms.userManager.getUser(uuid)

        if (user != null) {

            val prefixNode = user.nodes
                .filterIsInstance<PrefixNode>()
                .find { it.priority == 888 }

            if (prefixNode != null) {
                user.data().remove(prefixNode)
                luckPerms.userManager.saveUser(user)
            }
        }

    }

    fun getGroupsWithWeight(): Map<String, Int> {
        val groups = luckPerms.groupManager.loadedGroups
        val groupMap = mutableMapOf<String, Int>()

        for (group in groups) {
            val weight = group.weight.orElse(0)
            groupMap[group.name] = weight
        }

        return groupMap
    }

    fun getAllGroupsOfUuid(uuid: UUID, includeSubGroups: Boolean = false): List<String> {
        val user = luckPerms.userManager.getUser(uuid) ?: return emptyList()
        val groups = mutableSetOf<String>()

        // Add direct groups of the user
        groups.addAll(user.nodes.filterIsInstance<InheritanceNode>().map { it.groupName })

        if (includeSubGroups) {
            // Add subgroups of each group
            val groupManager = luckPerms.groupManager
            val subGroups = groups.flatMap { groupName ->
                groupManager.getGroup(groupName)?.nodes?.filterIsInstance<InheritanceNode>()?.map { it.groupName } ?: emptyList()
            }
            groups.addAll(subGroups)
        }

        return groups.toList()
    }

    fun getGroups() : List<String> {
        return luckPerms.groupManager.loadedGroups.map { it.name }
    }

    fun getAllPermissions(userUuid: UUID): List<String> {
        val userManager = luckPerms.userManager
        val groupManager = luckPerms.groupManager

        val user = userManager.loadUser(userUuid).join() ?: return emptyList()
        val userPermissions = getUserPermissions(user)

        val userGroups = user.getNodes(NodeType.INHERITANCE).mapNotNull { it.groupName }
        val groupPermissions = userGroups.flatMap { groupName ->
            groupManager.loadGroup(groupName).join().map { group ->
                getGroupPermissions(group)
            }.orElse(emptyList())
        }

        val highestGroup = userGroups.maxByOrNull { groupName ->
            groupManager.loadGroup(groupName).join().map { group ->
                group.weight.orElse(0)
            }.orElse(0)
        } ?: return userPermissions + groupPermissions

        val highestGroupSubGroups = groupManager.loadGroup(highestGroup).join().map { group ->
            group.getNodes(NodeType.INHERITANCE).mapNotNull { it.groupName }
        }.orElse(emptyList())

        val subGroupPermissions = highestGroupSubGroups.flatMap { groupName ->
            groupManager.loadGroup(groupName).join().map { group ->
                getGroupPermissions(group)
            }.orElse(emptyList())
        }

        return userPermissions + groupPermissions + subGroupPermissions
    }

    private fun getUserPermissions(user: User): List<String> {
        return user.getNodes(NodeType.PERMISSION).map { it.permission }
    }

    private fun getGroupPermissions(group: Group): List<String> {
        return group.getNodes(NodeType.PERMISSION).map { it.permission }
    }

    fun getPrefixOfGroupByName(groupName: String): String? {
        val group = luckPerms.groupManager.getGroup(groupName) ?: return null
        return group.cachedData.metaData.prefix
    }

    fun getLowestWeightGroupWithPermission(permission: String): String? {
        val groups = LuckPermsProvider.get().groupManager.loadedGroups

        // Filter groups that have the specified permission
        val groupsWithPermission = groups.filter { group ->
            group.nodes.any { node -> node.key == permission }
        }

        // Sort the filtered groups by weight in ascending order
        val sortedGroups = groupsWithPermission.sortedBy { group ->
            group.weight.orElse(Int.MAX_VALUE) // Use a high default weight if not set
        }

        // Return the name of the group with the lowest weight, or null if no group has the permission
        return sortedGroups.firstOrNull()?.name
    }

    fun addGroupToUuid(uuid: UUID, groupName: String) {
        val user = luckPerms.userManager.getUser(uuid)
        val group = luckPerms.groupManager.getGroup(groupName)

        if (user != null && group != null) {
            val node = InheritanceNode.builder(group.name).build()
            user.data().add(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun removeGroupFromUuid(uuid: UUID, groupName: String) {
        val user = luckPerms.userManager.getUser(uuid)
        val group = luckPerms.groupManager.getGroup(groupName)

        if (user != null && group != null) {
            val node = InheritanceNode.builder(group.name).build()
            user.data().remove(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun addTempGroupToUuid(uuid: UUID, groupName: String, duration: Long) {
        val user = luckPerms.userManager.getUser(uuid)
        val group = luckPerms.groupManager.getGroup(groupName)

        if (user != null && group != null) {
            val node = InheritanceNode.builder(group.name).expiry(duration).build()
            user.data().add(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun removeTempGroupFromUuid(uuid: UUID, groupName: String) {
        val user = luckPerms.userManager.getUser(uuid)
        val group = luckPerms.groupManager.getGroup(groupName)

        if (user != null && group != null) {
            val node = InheritanceNode.builder(group.name).build()
            user.data().remove(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun setPermissionOfUuid(uuid: UUID, permission: String, value: Boolean) {
        val user = luckPerms.userManager.getUser(uuid)

        if (user != null) {
            val node = if (value) {
                InheritanceNode.builder(permission).build()
            } else {
                InheritanceNode.builder(permission).build()
            }
            user.data().add(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun removePermissionOfUuid(uuid: UUID, permission: String) {
        val user = luckPerms.userManager.getUser(uuid)

        if (user != null) {
            val node = InheritanceNode.builder(permission).build()
            user.data().remove(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun setTempPermissionOfUuid(uuid: UUID, permission: String, value: Boolean, duration: Long) {
        val user = luckPerms.userManager.getUser(uuid)

        if (user != null) {
            val node = if (value) {
                InheritanceNode.builder(permission).expiry(duration).build()
            } else {
                InheritanceNode.builder(permission).expiry(duration).build()
            }
            user.data().add(node)
            luckPerms.userManager.saveUser(user)
        }
    }

    fun removeTempPermissionOfUuid(uuid: UUID, permission: String) {
        val user = luckPerms.userManager.getUser(uuid)

        if (user != null) {
            val node = InheritanceNode.builder(permission).build()
            user.data().remove(node)
            luckPerms.userManager.saveUser(user)
        }
    }


    fun addOrExtendTempGroupToUuid(uuid: UUID, groupName: String, durationSeconds: Long) {
        val user = luckPerms.userManager.getUser(uuid)
        val group = luckPerms.groupManager.getGroup(groupName)

        if (user != null && group != null) {
            val existingNode = user.getNodes(NodeType.INHERITANCE).find {
                it.groupName.equals(groupName, ignoreCase = true) && it.hasExpiry()
            }

            val now = Instant.now()
            val newExpiry = if (existingNode != null) {
                val existingExpiry = existingNode.expiry
                if (existingExpiry != null) {
                    existingExpiry.plus(durationSeconds, ChronoUnit.SECONDS)
                } else {
                    now.plus(durationSeconds, ChronoUnit.SECONDS)
                }
            } else {
                now.plus(durationSeconds, ChronoUnit.SECONDS)
            }

            // Alten Node entfernen (falls vorhanden)
            if (existingNode != null) {
                user.data().remove(existingNode)
            }

            // Neuen temporären Node setzen
            val newNode = InheritanceNode.builder(groupName)
                .expiry(newExpiry)
                .build()

            user.data().add(newNode)
            luckPerms.userManager.saveUser(user)
        }
    }
}