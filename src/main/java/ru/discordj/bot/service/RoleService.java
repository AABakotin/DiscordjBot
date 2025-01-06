package ru.discordj.bot.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final JDA jda;
    
    public RoleService(JDA jda) {
        this.jda = jda;
    }
    
    public void assignRole(String guildId, String userId, String roleId) {
        try {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) {
                logger.error("Guild not found: {}", guildId);
                return;
            }
            
            Role role = guild.getRoleById(roleId);
            if (role == null) {
                logger.error("Role not found: {}", roleId);
                return;
            }
            
            guild.retrieveMemberById(userId).queue(member -> {
                if (member != null) {
                    guild.addRoleToMember(member, role).queue(
                        success -> logger.info("Role {} assigned to user {} in guild {}", 
                            roleId, userId, guildId),
                        error -> logger.error("Failed to assign role: {}", error.getMessage())
                    );
                }
            });
        } catch (Exception e) {
            logger.error("Error assigning role: {}", e.getMessage());
        }
    }
    
    public void removeRole(String guildId, String userId, String roleId) {
        try {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) {
                logger.error("Guild not found: {}", guildId);
                return;
            }
            
            Role role = guild.getRoleById(roleId);
            if (role == null) {
                logger.error("Role not found: {}", roleId);
                return;
            }
            
            guild.retrieveMemberById(userId).queue(member -> {
                if (member != null) {
                    guild.removeRoleFromMember(member, role).queue(
                        success -> logger.info("Role {} removed from user {} in guild {}", 
                            roleId, userId, guildId),
                        error -> logger.error("Failed to remove role: {}", error.getMessage())
                    );
                }
            });
        } catch (Exception e) {
            logger.error("Error removing role: {}", e.getMessage());
        }
    }
} 