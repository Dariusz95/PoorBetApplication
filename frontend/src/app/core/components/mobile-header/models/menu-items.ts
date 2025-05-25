import { MobileMenuItem } from '../types/mobile-menu-item';

export const MENU_ITEMS: MobileMenuItem[] = [
  { labelKey: 'menu.home', link: '/', iconName: 'home' },
  {
    labelKey: 'menu.transactions',
    link: '/transactions',
    iconName: 'receipt_long',
  },
  { labelKey: 'menu.picks', link: '/picks', iconName: 'how_to_vote' },
  { labelKey: 'menu.settings', link: '/settings', iconName: 'settings' },
];
