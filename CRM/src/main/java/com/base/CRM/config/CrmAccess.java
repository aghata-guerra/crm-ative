package com.base.CRM.config;

import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CrmAccess {
    private CrmAccess() {}

    public static User currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("Entre para continuar.");
        }
        return user;
    }

    public static boolean isManager() {
        return currentUser().getProfile() == Profile.GESTOR;
    }

    public static void requireManager() {
        if (!isManager()) throw new AccessDeniedException("Esta operação exige o perfil gestor.");
    }

    public static void requireUser(Long userId) {
        if (userId == null || (!isManager() && !currentUser().getId().equals(userId))) {
            throw new AccessDeniedException("Você não tem acesso à carteira deste usuário.");
        }
    }

    public static boolean canView(Negociacao deal) {
        return isManager() || currentUser().getId().equals(deal.getUser().getId());
    }

    public static void requireDeal(Negociacao deal) {
        if (!canView(deal)) throw new AccessDeniedException("Você não tem acesso a esta negociação.");
    }
}
