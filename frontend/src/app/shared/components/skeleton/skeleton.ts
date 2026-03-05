import { Component, input, computed } from '@angular/core';

export type SkeletonType = 'text' | 'circle' | 'rectangle';

@Component({
  selector: 'app-skeleton',
  standalone: true,
  templateUrl: './skeleton.html',
  styleUrl: './skeleton.css',
})
export class Skeleton {
  // Inputs
  type = input<SkeletonType>('text');
  width = input<string>('100%');
  height = input<string>('1rem');
  count = input<number>(1);

  // Computed
  protected readonly skeletonClasses = computed(() => {
    const classes = ['skeleton'];
    classes.push(`skeleton-${this.type()}`);
    return classes.join(' ');
  });

  protected readonly items = computed(() =>
    Array.from({ length: this.count() }, (_, i) => i)
  );
}

