import { MenuItem }
from '../models/menu-item.model';

export const MENU_CONFIG:
Record<string, MenuItem[]> = {

  SUPER_ADMIN: [
    {
      label: 'Users',
      route: '/dashboard/users'
    },
    {
      label: 'Inventory',
      route: '/dashboard/inventory'
    },
    {
      label: 'Requests',
      route: '/dashboard/requests'
    },
    {
      label: 'Returns',
      route: '/dashboard/returns'
    }
  ],

  ADMIN: [
    {
      label: 'Users',
      route: '/dashboard/users'
    },
    {
      label: 'Inventory',
      route: '/dashboard/inventory'
    }
  ],

  INSTRUCTOR: [
    {
      label: 'Users',
      route: '/dashboard/users'
    },
    {
      label: 'Inventory',
      route: '/dashboard/inventory'
    },
    {
      label: 'Requests',
      route: '/dashboard/requests'
    },
    {
      label: 'Returns',
      route: '/dashboard/returns'
    }
  ],

  STUDENT: [
    {
      label: 'Components',
      route: '/dashboard/inventory'
    },
    {
      label: 'My Requests',
      route: '/dashboard/requests'
    },
    {
      label: 'My Returns',
      route: '/dashboard/returns'
    }
  ]
};