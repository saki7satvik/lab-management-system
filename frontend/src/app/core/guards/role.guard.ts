import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateFn, Router } from "@angular/router";
import { TokenService } from "../services/token";


export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) =>{
    const tokenService = inject(TokenService);
    const router = inject(Router);
    const allowedRoles = route.data['roles'];
    const userRole = tokenService.getRole();

    if(allowedRoles.includes(userRole)){
        return true;
    }

    router.navigate(['/login']);

    return false;
}