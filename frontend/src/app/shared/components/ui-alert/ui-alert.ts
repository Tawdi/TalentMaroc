import { Component, input, output, computed } from '@angular/core';

export type AlertType = 'info' | 'success' | 'warning' | 'error';

@Component({
  selector: 'app-ui-alert',
  standalone: true,
  templateUrl: './ui-alert.html',
  styleUrl: './ui-alert.css',
})
export class UiAlert {
  // Inputs
  type = input<AlertType>('info');
  dismissible = input<boolean>(false);
  title = input<string>('');

  // Outputs
  dismissed = output<void>();

  // Computed classes
  protected readonly alertClasses = computed(() => {
    const classes = ['alert'];
    classes.push(`alert-${this.type()}`);
    return classes.join(' ');
  });

  // Computed icon
  protected readonly icon = computed(() => {
    const type = this.type();
    const icons = {
      info: 'ℹ️',
      success: '✓',
      warning: '⚠️',
      error: '✕'
    };
    return icons[type] || icons.info;
  });

  handleDismiss(): void {
    this.dismissed.emit();
  }
}

