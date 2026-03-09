import { Component, input, model, computed, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-ui-textarea',
  standalone: true,
  templateUrl: './ui-textarea.html',
  styleUrl: './ui-textarea.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiTextarea {
  value = model<string>('');

  placeholder = input<string>('');
  label = input<string>('');
  errorText = input<string>('');
  helperText = input<string>('');
  required = input<boolean>(false);
  disabled = input<boolean>(false);
  rows = input<number>(4);
  id = input<string>(`textarea-${Math.random().toString(36).substr(2, 9)}`);

  protected readonly hasError = computed(() => !!this.errorText());

  protected readonly textareaClasses = computed(() => {
    const classes = ['form-textarea'];
    if (this.hasError()) classes.push('error');
    return classes.join(' ');
  });

  handleInput(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    this.value.set(target.value);
  }
}

