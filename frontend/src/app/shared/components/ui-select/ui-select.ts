import { Component, input, model, computed, ChangeDetectionStrategy, output } from '@angular/core';

export interface SelectOption {
  value: string;
  label: string;
}

@Component({
  selector: 'app-ui-select',
  standalone: true,
  templateUrl: './ui-select.html',
  styleUrl: './ui-select.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiSelect {
  value = model<string>('');

  options = input<SelectOption[]>([]);
  placeholder = input<string>('Select an option');
  label = input<string>('');
  errorText = input<string>('');
  required = input<boolean>(false);
  disabled = input<boolean>(false);
  id = input<string>(`select-${Math.random().toString(36).substr(2, 9)}`);

  changed = output<string>();

  protected readonly hasError = computed(() => !!this.errorText());

  protected readonly selectClasses = computed(() => {
    const classes = ['form-select'];
    if (this.hasError()) classes.push('error');
    return classes.join(' ');
  });

  handleChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.value.set(target.value);
    this.changed.emit(target.value);
  }
}

