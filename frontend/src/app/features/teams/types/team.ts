import { Uuid } from "@shared/types/uuid.type";

export interface Team {
  id: Uuid;
  name: string;
  logo: string | null;
  attackPower: number;
  defencePower: number;
}
