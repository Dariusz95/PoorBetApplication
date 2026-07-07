import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamService } from '@features/bet/services/team.service';
import {
  LiveMatchEvent,
  MatchEventType,
} from '@features/bet/types/match.types';
import { Uuid } from '@shared/types/uuid.type';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LiveMatchComponent } from './live-match.component';
import { LiveMatchTeamComponent } from '../live-match-team/live-match-team.component';

describe('LiveMatchComponent', () => {
  let component: LiveMatchComponent;
  let fixture: ComponentFixture<LiveMatchComponent>;

  const teamServiceMock = {
    getDetails: vi.fn(),
  };
  const mockLiveMatchEvent: LiveMatchEvent = {
    id: '123e4567-e89b-12d3-a456-426614174000' as Uuid,
    minute: 0,
    homeTeamId: '123e4567-e89b-12d3-a456-426614174001' as Uuid,
    awayTeamId: '123e4567-e89b-12d3-a456-426614174002' as Uuid,
    homeScore: 0,
    awayScore: 0,
    eventType: MatchEventType.Live,
    eventData: null,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LiveMatchComponent, getTranslocoModule()],
      providers: [{ provide: TeamService, useValue: teamServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(LiveMatchComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('liveMatch', mockLiveMatchEvent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render home and away team components with the correct team ids', () => {
    const teamComponents = fixture.debugElement.queryAll(
      (debugElement) =>
        debugElement.componentInstance instanceof LiveMatchTeamComponent,
    );

    expect(teamComponents).toHaveLength(2);
    expect(teamComponents[0].componentInstance.teamId()).toBe(
      mockLiveMatchEvent.homeTeamId,
    );
    expect(teamComponents[1].componentInstance.teamId()).toBe(
      mockLiveMatchEvent.awayTeamId,
    );
  });
});
