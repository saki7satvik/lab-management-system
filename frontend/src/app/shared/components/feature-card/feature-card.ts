// feature-card.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-feature-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div [routerLink]="link" class="feature-card glass-panel">
      <div class="card-icon" [style.color]="iconColor">{{ icon }}</div>
      <div class="card-info">
        <h3>{{ title }}</h3>
        <p>{{ description }}</p>
      </div>
    </div>
  `,
  styles: [`
    .feature-card {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      padding: 2rem;
      cursor: pointer;
      transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
      background: var(--glass-bg);
      border: 1px solid var(--glass-border);
      box-shadow: var(--glass-shadow);
      border-radius: var(--border-radius);
      
      &:hover {
        transform: translateY(-2px);
        border-color: rgba(255, 255, 255, 0.25);
        box-shadow: 0 12px 40px rgba(0,0,0,0.45);
      }
    }
    .card-icon { font-size: 2.5rem; display: flex; align-items: center; }
    .card-info {
      h3 { font-size: 1.25rem; font-weight: 600; margin-bottom: 0.25rem; color: var(--text-primary); }
      p { font-size: 0.9rem; color: var(--text-secondary); }
    }
  `]
})
export class FeatureCardComponent {
  @Input() title: string = '';
  @Input() description: string = '';
  @Input() icon: string = '';
  @Input() link: string = '';
  @Input() iconColor: string = 'var(--primary-cyan)';
}