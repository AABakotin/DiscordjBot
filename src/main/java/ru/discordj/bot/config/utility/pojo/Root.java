package ru.discordj.bot.config.utility.pojo;

import java.util.List;

public class Root {

    private String token;
    private String owner;
    private String invite_link;

    private List<Roles> roles;

    public Root() {
    }

    public Root(String token, String owner, String invite_link, List<Roles> roles) {
        this.token = token;
        this.owner = owner;
        this.invite_link = invite_link;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getInvite_link() {
        return invite_link;
    }

    public void setInvite_link(String invite_link) {
        this.invite_link = invite_link;
    }


    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Root{" +
                "token='" + token + '\'' +
                ", owner='" + owner + '\'' +
                ", invite_link='" + invite_link + '\'' +
                ", roles=" + roles +
                '}';
    }
}
