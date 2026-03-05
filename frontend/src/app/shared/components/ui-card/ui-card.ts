import { Component, input, computed } from '@angular/core';

@Component({
  selector: 'app-ui-card',
  standalone: true,
  templateUrl: './ui-card.html',
  styleUrl: './ui-card.css',
})
export class UiCard {
  // Inputs
  hoverable = input<boolean>(false);
  padding = input<'none' | 'sm' | 'md' | 'lg'>('md');

  // Computed classes
  protected readonly cardClasses = computed(() => {
    const classes = ['card'];

    if (this.hoverable()) {
      classes.push('card-hover');
    }

    if (this.padding() === 'none') {
      classes.push('card-no-padding');
    } else if (this.padding() === 'sm') {
      classes.push('card-padding-sm');
    } else if (this.padding() === 'lg') {
      classes.push('card-padding-lg');
    }

    return classes.join(' ');
  });
}

