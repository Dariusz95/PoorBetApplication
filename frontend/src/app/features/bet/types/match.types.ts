import { Uuid } from '@shared/types/uuid.type';

export interface LiveMatchEvent {
  id: Uuid;
  minute: number;
  homeTeamId: Uuid;
  awayTeamId: Uuid;
  homeScore: number;
  awayScore: number;
  eventType: MatchEventType;
  eventData: null | string;
}

export enum MatchEventType {
  MatchStarted = 'MATCH_STARTED',
  Live = 'LIVE',
  MatchEnded = 'MATCH_ENDED',
  Heartbeat = 'HEARTBEAT',
  MatchPoolFinished = 'MATCH_POOL_FINISHED',
}

export interface OddsDto {
  id: Uuid;
  homeWin: number;
  draw: number;
  awayWin: number;
}

export interface MatchDto {
  matchId: Uuid;
  homeTeamId: Uuid;
  awayTeamId: Uuid;
  odds: OddsDto;
}

export interface PoolMatch {
  id: Uuid;
  status: string;
  scheduledStartTime: string;
  matches: MatchDto[];
}

export interface ShortTeamInfo {
  id: Uuid;
  name: string;
}
