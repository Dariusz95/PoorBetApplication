import { Component, Input } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-auth-card-header',
  templateUrl: './auth-card-header.component.html',
  styleUrl: './auth-card-header.component.scss',
  standalone: true,
  imports: [TranslocoPipe],
})
export class AuthCardHeaderComponent {
  @Input() titleKey!: string;
  @Input() subtitleKey!: string;
}
