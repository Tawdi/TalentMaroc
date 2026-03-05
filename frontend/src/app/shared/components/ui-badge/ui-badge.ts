import { Component, input, computed } from '@angular/core';

export type BadgeVariant = 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';
export type BadgeSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'app-ui-badge',
  standalone: true,
  templateUrl: './ui-badge.html',
  styleUrl: './ui-badge.css',
})
export class UiBadge {
  // Inputs
  variant = input<BadgeVariant>('primary');
  size = input<BadgeSize>('md');
  pill = input<boolean>(false);

  // Computed classes
  protected readonly badgeClasses = computed(() => {
    const classes = ['badge'];
    classes.push(`badge-${this.variant()}`);
    classes.push(`badge-${this.size()}`);
    if (this.pill()) {
      classes.push('badge-pill');
    }
    return classes.join(' ');
  });
}

