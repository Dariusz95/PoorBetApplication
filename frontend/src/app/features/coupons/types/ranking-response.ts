import { PageResponse } from '@shared/interfaces/page-response';
import { RankingCoupon } from './ranking-coupon';

export interface RankingResponse {
  ranking: PageResponse<RankingCoupon>;
  lastUpdatedAt: string;
}
