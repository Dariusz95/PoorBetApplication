export interface DropdownOption<T = string> {
  value?: T;
  label: string;
  icon?: string;
  action?: () => void;
}
