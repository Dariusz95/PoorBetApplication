import { AsyncPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { Uuid } from '@shared/types/uuid.type';
import { Observable } from 'rxjs';
import { TeamService } from '../../services/team.service';
import { ShortTeamInfo } from '../../types/match.types';

@Component({
  selector: 'app-live-match-team',
  imports: [AsyncPipe],
  templateUrl: './live-match-team.component.html',
  styleUrl: './live-match-team.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchTeamComponent implements OnInit {
  private readonly teamService = inject(TeamService);

  teamId = input.required<Uuid>();

  team$!: Observable<ShortTeamInfo>;
  imgError = signal(false);

  ngOnInit(): void {
    this.team$ = this.teamService.getDetails(this.teamId());
  }
}
