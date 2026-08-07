import { Component, input, output, signal } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';

@Component({
  selector: 'app-team-logo-dropzone',
  imports: [TranslocoPipe, PbIconComponent],
  templateUrl: './team-logo-dropzone.component.html',
})
export class TeamLogoDropzoneComponent {
  previewUrl = input<string | null>(null);
  error = input(false);

  fileSelected = output<File | null>();
  cleared = output<void>();

  readonly isDragging = signal(false);

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
    this.fileSelected.emit(event.dataTransfer?.files?.[0] ?? null);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.fileSelected.emit(input.files?.[0] ?? null);
  }

  onClear(event: Event): void {
    event.stopPropagation();
    this.cleared.emit();
  }
}
