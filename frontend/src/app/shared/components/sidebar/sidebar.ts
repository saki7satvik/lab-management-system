import {
  Component,
  EventEmitter,
  Input,
  Output,
  inject
} from '@angular/core';

import {
  RouterLink,
  RouterLinkActive
} from '@angular/router';

import { CommonModule }
from '@angular/common';

import { MENU_CONFIG }
from '../../../core/constants/menu.config';



import { MenuItem }
from '../../../core/models/menu-item.model';
import { TokenService } from '../../../core/services/token';
import { ProfileMenu } from '../profile-menu/profile-menu';

@Component({
  selector: 'app-sidebar',
  standalone: true,

  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    ProfileMenu
  ],

  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class Sidebar {

  private tokenService =
    inject(TokenService);

  @Input() isOpen = false;

  @Output()
  closeSidebar =
    new EventEmitter<void>();

  menuItems: MenuItem[] = [];

  ngOnInit(): void {

    const role =
      this.tokenService.getRole();

    this.menuItems =
      MENU_CONFIG[
        role ?? 'STUDENT'
      ];
  }

  onCloseClick(): void {

    this.closeSidebar.emit();
  }
}