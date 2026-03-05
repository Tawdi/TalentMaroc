import { Component, input, output, computed } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger';
export type ButtonSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'app-ui-button',
  standalone: true,
  templateUrl: './ui-button.html',
  styleUrl: './ui-button.css',
})
export class UiButton {
  // Inputs using new signal-based API
  variant = input<ButtonVariant>('primary');
  size = input<ButtonSize>('md');
  disabled = input<boolean>(false);
  loading = input<boolean>(false);
  fullWidth = input<boolean>(false);
  type = input<'button' | 'submit' | 'reset'>('button');

  // Output
  clicked = output<MouseEvent>();

  // Computed CSS classes
  protected readonly buttonClasses = computed(() => {
    const classes = ['btn'];

    // Variant classes
    classes.push(`btn-${this.variant()}`);

    // Size classes
    classes.push(`btn-${this.size()}`);

    // Full width
    if (this.fullWidth()) {
      classes.push('btn-full');
    }

    return classes.join(' ');
  });

  // Computed disabled state (disabled or loading)
  protected readonly isDisabled = computed(() =>
    this.disabled() || this.loading()
  );

  handleClick(event: MouseEvent): void {
    if (!this.isDisabled()) {
      this.clicked.emit(event);
    }
  }
}
