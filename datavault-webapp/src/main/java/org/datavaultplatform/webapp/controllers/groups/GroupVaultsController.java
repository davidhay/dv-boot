package org.datavaultplatform.webapp.controllers.groups;


import java.util.ArrayList;
import org.datavaultplatform.common.model.Group;
import org.datavaultplatform.common.response.VaultInfo;
import org.datavaultplatform.webapp.services.RestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: Stuart Lewis
 * Date: 25/10/2015
 */

@Controller
@ConditionalOnBean(RestService.class)
public class GroupVaultsController {

    private final RestService restService;

    public GroupVaultsController(RestService restService) {
        this.restService = restService;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public String getGroupVaultsListing(ModelMap model) throws Exception {
        // Which groups is this user an owner of
        Group[] groups = restService.getGroups();
        ArrayList<Group> members = new ArrayList<>();
        for (Group group : groups) {
            if (group.hasMember(SecurityContextHolder.getContext().getAuthentication().getName())) {
                members.add(group);
            }
        }

        // Get the vaults for each group
        ArrayList<VaultInfo[]> vaults = new ArrayList<>();
        for (Group group : members) {
            VaultInfo[] v = restService.getVaultsListingForGroup(group.getID());
            vaults.add(v);
        }

        model.addAttribute("vaults", vaults);
        model.addAttribute("groups", members);
        return "groups/index";
    }
}


