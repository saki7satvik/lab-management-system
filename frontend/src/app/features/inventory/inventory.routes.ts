import { Route } from "@angular/router";
import { ComponentListPage } from "./pages/component-list-page/component-list-page";
import { CreateComponentPage } from "./pages/create-component-page/create-component-page";
import { InventoryHomePage } from "./pages/inventory-home-page/inventory-home-page";


export const INVENTORY_ROUTES: Route[] = [
    {
        path: '',
        component: InventoryHomePage
    },
    {
        path: 'list',
        component: ComponentListPage
    },
    {
        path: 'create',
        component: CreateComponentPage
    }
];