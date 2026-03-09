import { Component, input, computed } from '@angular/core';

export type SpinnerSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl';
export type SpinnerColor = 'primary' | 'secondary' | 'white' | 'current';

@Component({
  selector: 'app-spinner',
  standalone: true,
  templateUrl: './spinner.html',
  styleUrl: './spinner.css',
})
export class Spinner {
  // Inputs
  size = input<SpinnerSize>('md');
  color = input<SpinnerColor>('primary');
  fullScreen = input<boolean>(false);
  label = input<string>('');

  // Computed classes
  protected readonly spinnerClasses = computed(() => {
    const classes = ['spinner'];
    classes.push(`spinner-${this.size()}`);
    classes.push(`spinner-${this.color()}`);
    return classes.join(' ');
  });

  protected readonly containerClasses = computed(() => {
    const classes = ['spinner-container'];
    if (this.fullScreen()) {
      classes.push('spinner-fullscreen');
    }
    return classes.join(' ');
  });
}
