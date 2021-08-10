package com.silverpine.uu.bluetooth;

import java.util.HashMap;
import java.util.UUID;

/**
 * Common bluetooth related constants
 */
@SuppressWarnings("unused")
public class UUBluetoothConstants
{
    /**
     * String formatter for building full 128 UUID's from a 16 bit BTLE shortcode
     */
    public static final String BLUETOOTH_UUID_SHORTCODE_FORMAT = "0000%s-0000-1000-8000-00805F9B34FB";

    /**
     * This status code gets returned from BluetoothGattCallback.onConnectionStateChanged, but is not
     * defined anywhere.  The constant is defined here:
     *
     * https://android.googlesource.com/platform/external/bluetooth/bluedroid/+/master/stack/include/gatt_api.h
     *
     * #define  GATT_ERROR                          0x85
     */
    public static final int GATT_ERROR = 0x85; // Status 133

    /**
     * When a peripheral terminates a connection, BluetoothGattCallback.onConnectionStateChanged is
     * called with status 19.  The constant is defined here:
     *
     * https://android.googlesource.com/platform/external/bluetooth/bluedroid/+/master/stack/include/gatt_api.h
     *
     * #define GATT_CONN_TERMINATE_PEER_USER       HCI_ERR_PEER_USER               0x13 connection terminate by peer user
     *
     * And HCI_ERR_PEER_USER is defined:
     *
     * https://android.googlesource.com/platform/external/bluetooth/bluedroid/+/master/stack/include/hcidefs.h
     *
     * #define HCI_ERR_PEER_USER                               0x13
     */
    public static final int GATT_DISCONNECTED_BY_PERIPHERAL = 0x13;

    public static class Services
    {
        public static final UUID SERIAL_PORT_PROFILE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        /**
         * SpecificationName: Alert Notification Service
         * SpecificationType: org.bluetooth.service.alert_notification
         * AssignedNumber: 0x1811
         */
        public static final UUID ALERT_NOTIFICATION_SERVICE_UUID = UUBluetooth.shortCodeToUuid("1811");

        /**
         * SpecificationName: Automation IO
         * SpecificationType: org.bluetooth.service.automation_io
         * AssignedNumber: 0x1815
         */
        public static final UUID AUTOMATION_IO_UUID = UUBluetooth.shortCodeToUuid("1815");

        /**
         * SpecificationName: Battery Service
         * SpecificationType: org.bluetooth.service.battery_service
         * AssignedNumber: 0x180F
         */
        public static final UUID BATTERY_SERVICE_UUID = UUBluetooth.shortCodeToUuid("180F");

        /**
         * SpecificationName: Blood Pressure
         * SpecificationType: org.bluetooth.service.blood_pressure
         * AssignedNumber: 0x1810
         */
        public static final UUID BLOOD_PRESSURE_UUID = UUBluetooth.shortCodeToUuid("1810");

        /**
         * SpecificationName: Body Composition
         * SpecificationType: org.bluetooth.service.body_composition
         * AssignedNumber: 0x181B
         */
        public static final UUID BODY_COMPOSITION_UUID = UUBluetooth.shortCodeToUuid("181B");

        /**
         * SpecificationName: Bond Management
         * SpecificationType: org.bluetooth.service.bond_management
         * AssignedNumber: 0x181E
         */
        public static final UUID BOND_MANAGEMENT_UUID = UUBluetooth.shortCodeToUuid("181E");

        /**
         * SpecificationName: Continuous Glucose Monitoring
         * SpecificationType: org.bluetooth.service.continuous_glucose_monitoring
         * AssignedNumber: 0x181F
         */
        public static final UUID CONTINUOUS_GLUCOSE_MONITORING_UUID = UUBluetooth.shortCodeToUuid("181F");

        /**
         * SpecificationName: Current Time Service
         * SpecificationType: org.bluetooth.service.current_time
         * AssignedNumber: 0x1805
         */
        public static final UUID CURRENT_TIME_SERVICE_UUID = UUBluetooth.shortCodeToUuid("1805");

        /**
         * SpecificationName: Cycling Power
         * SpecificationType: org.bluetooth.service.cycling_power
         * AssignedNumber: 0x1818
         */
        public static final UUID CYCLING_POWER_UUID = UUBluetooth.shortCodeToUuid("1818");

        /**
         * SpecificationName: Cycling Speed and Cadence
         * SpecificationType: org.bluetooth.service.cycling_speed_and_cadence
         * AssignedNumber: 0x1816
         */
        public static final UUID CYCLING_SPEED_AND_CADENCE_UUID = UUBluetooth.shortCodeToUuid("1816");

        /**
         * SpecificationName: Device Information
         * SpecificationType: org.bluetooth.service.device_information
         * AssignedNumber: 0x180A
         */
        public static final UUID DEVICE_INFORMATION_UUID = UUBluetooth.shortCodeToUuid("180A");

        /**
         * SpecificationName: Environmental Sensing
         * SpecificationType: org.bluetooth.service.environmental_sensing
         * AssignedNumber: 0x181A
         */
        public static final UUID ENVIRONMENTAL_SENSING_UUID = UUBluetooth.shortCodeToUuid("181A");

        /**
         * SpecificationName: Generic Access
         * SpecificationType: org.bluetooth.service.generic_access
         * AssignedNumber: 0x1800
         */
        public static final UUID GENERIC_ACCESS_UUID = UUBluetooth.shortCodeToUuid("1800");

        /**
         * SpecificationName: Generic Attribute
         * SpecificationType: org.bluetooth.service.generic_attribute
         * AssignedNumber: 0x1801
         */
        public static final UUID GENERIC_ATTRIBUTE_UUID = UUBluetooth.shortCodeToUuid("1801");

        /**
         * SpecificationName: Glucose
         * SpecificationType: org.bluetooth.service.glucose
         * AssignedNumber: 0x1808
         */
        public static final UUID GLUCOSE_UUID = UUBluetooth.shortCodeToUuid("1808");

        /**
         * SpecificationName: Health Thermometer
         * SpecificationType: org.bluetooth.service.health_thermometer
         * AssignedNumber: 0x1809
         */
        public static final UUID HEALTH_THERMOMETER_UUID = UUBluetooth.shortCodeToUuid("1809");

        /**
         * SpecificationName: Heart Rate
         * SpecificationType: org.bluetooth.service.heart_rate
         * AssignedNumber: 0x180D
         */
        public static final UUID HEART_RATE_UUID = UUBluetooth.shortCodeToUuid("180D");

        /**
         * SpecificationName: HTTP Proxy
         * SpecificationType: org.bluetooth.service.http_proxy
         * AssignedNumber: 0x1823
         */
        public static final UUID HTTP_PROXY_UUID = UUBluetooth.shortCodeToUuid("1823");

        /**
         * SpecificationName: Human Interface Device
         * SpecificationType: org.bluetooth.service.human_interface_device
         * AssignedNumber: 0x1812
         */
        public static final UUID HUMAN_INTERFACE_DEVICE_UUID = UUBluetooth.shortCodeToUuid("1812");

        /**
         * SpecificationName: Immediate Alert
         * SpecificationType: org.bluetooth.service.immediate_alert
         * AssignedNumber: 0x1802
         */
        public static final UUID IMMEDIATE_ALERT_UUID = UUBluetooth.shortCodeToUuid("1802");

        /**
         * SpecificationName: Indoor Positioning
         * SpecificationType: org.bluetooth.service.indoor_positioning
         * AssignedNumber: 0x1821
         */
        public static final UUID INDOOR_POSITIONING_UUID = UUBluetooth.shortCodeToUuid("1821");

        /**
         * SpecificationName: Internet Protocol Support
         * SpecificationType: org.bluetooth.service.internet_protocol_support
         * AssignedNumber: 0x1820
         */
        public static final UUID INTERNET_PROTOCOL_SUPPORT_UUID = UUBluetooth.shortCodeToUuid("1820");

        /**
         * SpecificationName: Link Loss
         * SpecificationType: org.bluetooth.service.link_loss
         * AssignedNumber: 0x1803
         */
        public static final UUID LINK_LOSS_UUID = UUBluetooth.shortCodeToUuid("1803");

        /**
         * SpecificationName: Location and Navigation
         * SpecificationType: org.bluetooth.service.location_and_navigation
         * AssignedNumber: 0x1819
         */
        public static final UUID LOCATION_AND_NAVIGATION_UUID = UUBluetooth.shortCodeToUuid("1819");

        /**
         * SpecificationName: Next DST Change Service
         * SpecificationType: org.bluetooth.service.next_dst_change
         * AssignedNumber: 0x1807
         */
        public static final UUID NEXT_DST_CHANGE_SERVICE_UUID = UUBluetooth.shortCodeToUuid("1807");

        /**
         * SpecificationName: Object Transfer
         * SpecificationType: org.bluetooth.service.object_transfer
         * AssignedNumber: 0x1825
         */
        public static final UUID OBJECT_TRANSFER_UUID = UUBluetooth.shortCodeToUuid("1825");

        /**
         * SpecificationName: Phone Alert Status Service
         * SpecificationType: org.bluetooth.service.phone_alert_status
         * AssignedNumber: 0x180E
         */
        public static final UUID PHONE_ALERT_STATUS_SERVICE_UUID = UUBluetooth.shortCodeToUuid("180E");

        /**
         * SpecificationName: Pulse Oximeter
         * SpecificationType: org.bluetooth.service.pulse_oximeter
         * AssignedNumber: 0x1822
         */
        public static final UUID PULSE_OXIMETER_UUID = UUBluetooth.shortCodeToUuid("1822");

        /**
         * SpecificationName: Reference Time Update Service
         * SpecificationType: org.bluetooth.service.reference_time_update
         * AssignedNumber: 0x1806
         */
        public static final UUID REFERENCE_TIME_UPDATE_SERVICE_UUID = UUBluetooth.shortCodeToUuid("1806");

        /**
         * SpecificationName: Running Speed and Cadence
         * SpecificationType: org.bluetooth.service.running_speed_and_cadence
         * AssignedNumber: 0x1814
         */
        public static final UUID RUNNING_SPEED_AND_CADENCE_UUID = UUBluetooth.shortCodeToUuid("1814");

        /**
         * SpecificationName: Scan Parameters
         * SpecificationType: org.bluetooth.service.scan_parameters
         * AssignedNumber: 0x1813
         */
        public static final UUID SCAN_PARAMETERS_UUID = UUBluetooth.shortCodeToUuid("1813");

        /**
         * SpecificationName: Transport Discovery
         * SpecificationType: org.bluetooth.service.transport_discovery
         * AssignedNumber: 0x1824
         */
        public static final UUID TRANSPORT_DISCOVERY_UUID = UUBluetooth.shortCodeToUuid("1824");

        /**
         * SpecificationName: Tx Power
         * SpecificationType: org.bluetooth.service.tx_power
         * AssignedNumber: 0x1804
         */
        public static final UUID TX_POWER_UUID = UUBluetooth.shortCodeToUuid("1804");

        /**
         * SpecificationName: User Data
         * SpecificationType: org.bluetooth.service.user_data
         * AssignedNumber: 0x181C
         */
        public static final UUID USER_DATA_UUID = UUBluetooth.shortCodeToUuid("181C");

        /**
         * SpecificationName: Weight Scale
         * SpecificationType: org.bluetooth.service.weight_scale
         * AssignedNumber: 0x181D
         */
        public static final UUID WEIGHT_SCALE_UUID = UUBluetooth.shortCodeToUuid("181D");

    }

    public static class Characteristics
    {
        /**
         * SpecificationName: Aerobic Heart Rate Lower Limit
         * SpecificationType: org.bluetooth.characteristic.aerobic_heart_rate_lower_limit
         * AssignedNumber: 0x2A7E
         */
        public static final UUID AEROBIC_HEART_RATE_LOWER_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A7E");

        /**
         * SpecificationName: Aerobic Heart Rate Upper Limit
         * SpecificationType: org.bluetooth.characteristic.aerobic_heart_rate_upper_limit
         * AssignedNumber: 0x2A84
         */
        public static final UUID AEROBIC_HEART_RATE_UPPER_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A84");

        /**
         * SpecificationName: Aerobic Threshold
         * SpecificationType: org.bluetooth.characteristic.aerobic_threshold
         * AssignedNumber: 0x2A7F
         */
        public static final UUID AEROBIC_THRESHOLD_UUID = UUBluetooth.shortCodeToUuid("2A7F");

        /**
         * SpecificationName: Age
         * SpecificationType: org.bluetooth.characteristic.age
         * AssignedNumber: 0x2A80
         */
        public static final UUID AGE_UUID = UUBluetooth.shortCodeToUuid("2A80");

        /**
         * SpecificationName: Aggregate
         * SpecificationType: org.bluetooth.characteristic.aggregate
         * AssignedNumber: 0x2A5A
         */
        public static final UUID AGGREGATE_UUID = UUBluetooth.shortCodeToUuid("2A5A");

        /**
         * SpecificationName: Alert Category ID
         * SpecificationType: org.bluetooth.characteristic.alert_category_id
         * AssignedNumber: 0x2A43
         */
        public static final UUID ALERT_CATEGORY_ID_UUID = UUBluetooth.shortCodeToUuid("2A43");

        /**
         * SpecificationName: Alert Category ID Bit Mask
         * SpecificationType: org.bluetooth.characteristic.alert_category_id_bit_mask
         * AssignedNumber: 0x2A42
         */
        public static final UUID ALERT_CATEGORY_ID_BIT_MASK_UUID = UUBluetooth.shortCodeToUuid("2A42");

        /**
         * SpecificationName: Alert Level
         * SpecificationType: org.bluetooth.characteristic.alert_level
         * AssignedNumber: 0x2A06
         */
        public static final UUID ALERT_LEVEL_UUID = UUBluetooth.shortCodeToUuid("2A06");

        /**
         * SpecificationName: Alert Notification Control Point
         * SpecificationType: org.bluetooth.characteristic.alert_notification_control_point
         * AssignedNumber: 0x2A44
         */
        public static final UUID ALERT_NOTIFICATION_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A44");

        /**
         * SpecificationName: Alert Status
         * SpecificationType: org.bluetooth.characteristic.alert_status
         * AssignedNumber: 0x2A3F
         */
        public static final UUID ALERT_STATUS_UUID = UUBluetooth.shortCodeToUuid("2A3F");

        /**
         * SpecificationName: Altitude
         * SpecificationType: org.bluetooth.characteristic.altitude
         * AssignedNumber: 0x2AB3
         */
        public static final UUID ALTITUDE_UUID = UUBluetooth.shortCodeToUuid("2AB3");

        /**
         * SpecificationName: Anaerobic Heart Rate Lower Limit
         * SpecificationType: org.bluetooth.characteristic.anaerobic_heart_rate_lower_limit
         * AssignedNumber: 0x2A81
         */
        public static final UUID ANAEROBIC_HEART_RATE_LOWER_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A81");

        /**
         * SpecificationName: Anaerobic Heart Rate Upper Limit
         * SpecificationType: org.bluetooth.characteristic.anaerobic_heart_rate_upper_limit
         * AssignedNumber: 0x2A82
         */
        public static final UUID ANAEROBIC_HEART_RATE_UPPER_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A82");

        /**
         * SpecificationName: Anaerobic Threshold
         * SpecificationType: org.bluetooth.characteristic.anaerobic_threshold
         * AssignedNumber: 0x2A83
         */
        public static final UUID ANAEROBIC_THRESHOLD_UUID = UUBluetooth.shortCodeToUuid("2A83");

        /**
         * SpecificationName: Analog
         * SpecificationType: org.bluetooth.characteristic.analog
         * AssignedNumber: 0x2A58
         */
        public static final UUID ANALOG_UUID = UUBluetooth.shortCodeToUuid("2A58");

        /**
         * SpecificationName: Apparent Wind Direction
         * SpecificationType: org.bluetooth.characteristic.apparent_wind_direction
         * AssignedNumber: 0x2A73
         */
        public static final UUID APPARENT_WIND_DIRECTION_UUID = UUBluetooth.shortCodeToUuid("2A73");

        /**
         * SpecificationName: Apparent Wind Speed
         * SpecificationType: org.bluetooth.characteristic.apparent_wind_speed
         * AssignedNumber: 0x2A72
         */
        public static final UUID APPARENT_WIND_SPEED_UUID = UUBluetooth.shortCodeToUuid("2A72");

        /**
         * SpecificationName: Appearance
         * SpecificationType: org.bluetooth.characteristic.gap.appearance
         * AssignedNumber: 0x2A01
         */
        public static final UUID APPEARANCE_UUID = UUBluetooth.shortCodeToUuid("2A01");

        /**
         * SpecificationName: Barometric Pressure Trend
         * SpecificationType: org.bluetooth.characteristic.barometric_pressure_trend
         * AssignedNumber: 0x2AA3
         */
        public static final UUID BAROMETRIC_PRESSURE_TREND_UUID = UUBluetooth.shortCodeToUuid("2AA3");

        /**
         * SpecificationName: Battery Level
         * SpecificationType: org.bluetooth.characteristic.battery_level
         * AssignedNumber: 0x2A19
         */
        public static final UUID BATTERY_LEVEL_UUID = UUBluetooth.shortCodeToUuid("2A19");

        /**
         * SpecificationName: Blood Pressure Feature
         * SpecificationType: org.bluetooth.characteristic.blood_pressure_feature
         * AssignedNumber: 0x2A49
         */
        public static final UUID BLOOD_PRESSURE_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A49");

        /**
         * SpecificationName: Blood Pressure Measurement
         * SpecificationType: org.bluetooth.characteristic.blood_pressure_measurement
         * AssignedNumber: 0x2A35
         */
        public static final UUID BLOOD_PRESSURE_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A35");

        /**
         * SpecificationName: Body Composition Feature
         * SpecificationType: org.bluetooth.characteristic.body_composition_feature
         * AssignedNumber: 0x2A9B
         */
        public static final UUID BODY_COMPOSITION_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A9B");

        /**
         * SpecificationName: Body Composition Measurement
         * SpecificationType: org.bluetooth.characteristic.body_composition_measurement
         * AssignedNumber: 0x2A9C
         */
        public static final UUID BODY_COMPOSITION_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A9C");

        /**
         * SpecificationName: Body Sensor Location
         * SpecificationType: org.bluetooth.characteristic.body_sensor_location
         * AssignedNumber: 0x2A38
         */
        public static final UUID BODY_SENSOR_LOCATION_UUID = UUBluetooth.shortCodeToUuid("2A38");

        /**
         * SpecificationName: Bond Management Control Point
         * SpecificationType: org.bluetooth.characteristic.bond_management_control_point
         * AssignedNumber: 0x2AA4
         */
        public static final UUID BOND_MANAGEMENT_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2AA4");

        /**
         * SpecificationName: Bond Management Feature
         * SpecificationType: org.bluetooth.characteristic.bond_management_feature
         * AssignedNumber: 0x2AA5
         */
        public static final UUID BOND_MANAGEMENT_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2AA5");

        /**
         * SpecificationName: Boot Keyboard Input Report
         * SpecificationType: org.bluetooth.characteristic.boot_keyboard_input_report
         * AssignedNumber: 0x2A22
         */
        public static final UUID BOOT_KEYBOARD_INPUT_REPORT_UUID = UUBluetooth.shortCodeToUuid("2A22");

        /**
         * SpecificationName: Boot Keyboard Output Report
         * SpecificationType: org.bluetooth.characteristic.boot_keyboard_output_report
         * AssignedNumber: 0x2A32
         */
        public static final UUID BOOT_KEYBOARD_OUTPUT_REPORT_UUID = UUBluetooth.shortCodeToUuid("2A32");

        /**
         * SpecificationName: Boot Mouse Input Report
         * SpecificationType: org.bluetooth.characteristic.boot_mouse_input_report
         * AssignedNumber: 0x2A33
         */
        public static final UUID BOOT_MOUSE_INPUT_REPORT_UUID = UUBluetooth.shortCodeToUuid("2A33");

        /**
         * SpecificationName: Central Address Resolution
         * SpecificationType: org.bluetooth.characteristic.gap.central_address_resolution_support
         * AssignedNumber: 0x2AA6
         */
        public static final UUID CENTRAL_ADDRESS_RESOLUTION_UUID = UUBluetooth.shortCodeToUuid("2AA6");

        /**
         * SpecificationName: CGM Feature
         * SpecificationType: org.bluetooth.characteristic.cgm_feature
         * AssignedNumber: 0x2AA8
         */
        public static final UUID CGM_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2AA8");

        /**
         * SpecificationName: CGM Measurement
         * SpecificationType: org.bluetooth.characteristic.cgm_measurement
         * AssignedNumber: 0x2AA7
         */
        public static final UUID CGM_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2AA7");

        /**
         * SpecificationName: CGM Session Run Time
         * SpecificationType: org.bluetooth.characteristic.cgm_session_run_time
         * AssignedNumber: 0x2AAB
         */
        public static final UUID CGM_SESSION_RUN_TIME_UUID = UUBluetooth.shortCodeToUuid("2AAB");

        /**
         * SpecificationName: CGM Session Start Time
         * SpecificationType: org.bluetooth.characteristic.cgm_session_start_time
         * AssignedNumber: 0x2AAA
         */
        public static final UUID CGM_SESSION_START_TIME_UUID = UUBluetooth.shortCodeToUuid("2AAA");

        /**
         * SpecificationName: CGM Specific Ops Control Point
         * SpecificationType: org.bluetooth.characteristic.cgm_specific_ops_control_point
         * AssignedNumber: 0x2AAC
         */
        public static final UUID CGM_SPECIFIC_OPS_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2AAC");

        /**
         * SpecificationName: CGM Status
         * SpecificationType: org.bluetooth.characteristic.cgm_status
         * AssignedNumber: 0x2AA9
         */
        public static final UUID CGM_STATUS_UUID = UUBluetooth.shortCodeToUuid("2AA9");

        /**
         * SpecificationName: CSC Feature
         * SpecificationType: org.bluetooth.characteristic.csc_feature
         * AssignedNumber: 0x2A5C
         */
        public static final UUID CSC_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A5C");

        /**
         * SpecificationName: CSC Measurement
         * SpecificationType: org.bluetooth.characteristic.csc_measurement
         * AssignedNumber: 0x2A5B
         */
        public static final UUID CSC_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A5B");

        /**
         * SpecificationName: Current Time
         * SpecificationType: org.bluetooth.characteristic.current_time
         * AssignedNumber: 0x2A2B
         */
        public static final UUID CURRENT_TIME_UUID = UUBluetooth.shortCodeToUuid("2A2B");

        /**
         * SpecificationName: Cycling Power Control Point
         * SpecificationType: org.bluetooth.characteristic.cycling_power_control_point
         * AssignedNumber: 0x2A66
         */
        public static final UUID CYCLING_POWER_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A66");

        /**
         * SpecificationName: Cycling Power Feature
         * SpecificationType: org.bluetooth.characteristic.cycling_power_feature
         * AssignedNumber: 0x2A65
         */
        public static final UUID CYCLING_POWER_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A65");

        /**
         * SpecificationName: Cycling Power Measurement
         * SpecificationType: org.bluetooth.characteristic.cycling_power_measurement
         * AssignedNumber: 0x2A63
         */
        public static final UUID CYCLING_POWER_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A63");

        /**
         * SpecificationName: Cycling Power Vector
         * SpecificationType: org.bluetooth.characteristic.cycling_power_vector
         * AssignedNumber: 0x2A64
         */
        public static final UUID CYCLING_POWER_VECTOR_UUID = UUBluetooth.shortCodeToUuid("2A64");

        /**
         * SpecificationName: Database Change Increment
         * SpecificationType: org.bluetooth.characteristic.database_change_increment
         * AssignedNumber: 0x2A99
         */
        public static final UUID DATABASE_CHANGE_INCREMENT_UUID = UUBluetooth.shortCodeToUuid("2A99");

        /**
         * SpecificationName: Date of Birth
         * SpecificationType: org.bluetooth.characteristic.date_of_birth
         * AssignedNumber: 0x2A85
         */
        public static final UUID DATE_OF_BIRTH_UUID = UUBluetooth.shortCodeToUuid("2A85");

        /**
         * SpecificationName: Date of Threshold Assessment
         * SpecificationType: org.bluetooth.characteristic.date_of_threshold_assessment
         * AssignedNumber: 0x2A86
         */
        public static final UUID DATE_OF_THRESHOLD_ASSESSMENT_UUID = UUBluetooth.shortCodeToUuid("2A86");

        /**
         * SpecificationName: Date Time
         * SpecificationType: org.bluetooth.characteristic.date_time
         * AssignedNumber: 0x2A08
         */
        public static final UUID DATE_TIME_UUID = UUBluetooth.shortCodeToUuid("2A08");

        /**
         * SpecificationName: Day Date Time
         * SpecificationType: org.bluetooth.characteristic.day_date_time
         * AssignedNumber: 0x2A0A
         */
        public static final UUID DAY_DATE_TIME_UUID = UUBluetooth.shortCodeToUuid("2A0A");

        /**
         * SpecificationName: Day of Week
         * SpecificationType: org.bluetooth.characteristic.day_of_week
         * AssignedNumber: 0x2A09
         */
        public static final UUID DAY_OF_WEEK_UUID = UUBluetooth.shortCodeToUuid("2A09");

        /**
         * SpecificationName: Descriptor Value Changed
         * SpecificationType: org.bluetooth.characteristic.descriptor_value_changed
         * AssignedNumber: 0x2A7D
         */
        public static final UUID DESCRIPTOR_VALUE_CHANGED_UUID = UUBluetooth.shortCodeToUuid("2A7D");

        /**
         * SpecificationName: Device Name
         * SpecificationType: org.bluetooth.characteristic.gap.device_name
         * AssignedNumber: 0x2A00
         */
        public static final UUID DEVICE_NAME_UUID = UUBluetooth.shortCodeToUuid("2A00");

        /**
         * SpecificationName: Dew Point
         * SpecificationType: org.bluetooth.characteristic.dew_point
         * AssignedNumber: 0x2A7B
         */
        public static final UUID DEW_POINT_UUID = UUBluetooth.shortCodeToUuid("2A7B");

        /**
         * SpecificationName: Digital
         * SpecificationType: org.bluetooth.characteristic.digital
         * AssignedNumber: 0x2A56
         */
        public static final UUID DIGITAL_UUID = UUBluetooth.shortCodeToUuid("2A56");

        /**
         * SpecificationName: DST Offset
         * SpecificationType: org.bluetooth.characteristic.dst_offset
         * AssignedNumber: 0x2A0D
         */
        public static final UUID DST_OFFSET_UUID = UUBluetooth.shortCodeToUuid("2A0D");

        /**
         * SpecificationName: Elevation
         * SpecificationType: org.bluetooth.characteristic.elevation
         * AssignedNumber: 0x2A6C
         */
        public static final UUID ELEVATION_UUID = UUBluetooth.shortCodeToUuid("2A6C");

        /**
         * SpecificationName: Email Address
         * SpecificationType: org.bluetooth.characteristic.email_address
         * AssignedNumber: 0x2A87
         */
        public static final UUID EMAIL_ADDRESS_UUID = UUBluetooth.shortCodeToUuid("2A87");

        /**
         * SpecificationName: Exact Time 256
         * SpecificationType: org.bluetooth.characteristic.exact_time_256
         * AssignedNumber: 0x2A0C
         */
        public static final UUID EXACT_TIME_256_UUID = UUBluetooth.shortCodeToUuid("2A0C");

        /**
         * SpecificationName: Fat Burn Heart Rate Lower Limit
         * SpecificationType: org.bluetooth.characteristic.fat_burn_heart_rate_lower_limit
         * AssignedNumber: 0x2A88
         */
        public static final UUID FAT_BURN_HEART_RATE_LOWER_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A88");

        /**
         * SpecificationName: Fat Burn Heart Rate Upper Limit
         * SpecificationType: org.bluetooth.characteristic.fat_burn_heart_rate_upper_limit
         * AssignedNumber: 0x2A89
         */
        public static final UUID FAT_BURN_HEART_RATE_UPPER_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A89");

        /**
         * SpecificationName: Firmware Revision String
         * SpecificationType: org.bluetooth.characteristic.firmware_revision_string
         * AssignedNumber: 0x2A26
         */
        public static final UUID FIRMWARE_REVISION_STRING_UUID = UUBluetooth.shortCodeToUuid("2A26");

        /**
         * SpecificationName: First Name
         * SpecificationType: org.bluetooth.characteristic.first_name
         * AssignedNumber: 0x2A8A
         */
        public static final UUID FIRST_NAME_UUID = UUBluetooth.shortCodeToUuid("2A8A");

        /**
         * SpecificationName: Five Zone Heart Rate Limits
         * SpecificationType: org.bluetooth.characteristic.five_zone_heart_rate_limits
         * AssignedNumber: 0x2A8B
         */
        public static final UUID FIVE_ZONE_HEART_RATE_LIMITS_UUID = UUBluetooth.shortCodeToUuid("2A8B");

        /**
         * SpecificationName: Floor Number
         * SpecificationType: org.bluetooth.characteristic.floor_number
         * AssignedNumber: 0x2AB2
         */
        public static final UUID FLOOR_NUMBER_UUID = UUBluetooth.shortCodeToUuid("2AB2");

        /**
         * SpecificationName: Gender
         * SpecificationType: org.bluetooth.characteristic.gender
         * AssignedNumber: 0x2A8C
         */
        public static final UUID GENDER_UUID = UUBluetooth.shortCodeToUuid("2A8C");

        /**
         * SpecificationName: Glucose Feature
         * SpecificationType: org.bluetooth.characteristic.glucose_feature
         * AssignedNumber: 0x2A51
         */
        public static final UUID GLUCOSE_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A51");

        /**
         * SpecificationName: Glucose Measurement
         * SpecificationType: org.bluetooth.characteristic.glucose_measurement
         * AssignedNumber: 0x2A18
         */
        public static final UUID GLUCOSE_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A18");

        /**
         * SpecificationName: Glucose Measurement Context
         * SpecificationType: org.bluetooth.characteristic.glucose_measurement_context
         * AssignedNumber: 0x2A34
         */
        public static final UUID GLUCOSE_MEASUREMENT_CONTEXT_UUID = UUBluetooth.shortCodeToUuid("2A34");

        /**
         * SpecificationName: Gust Factor
         * SpecificationType: org.bluetooth.characteristic.gust_factor
         * AssignedNumber: 0x2A74
         */
        public static final UUID GUST_FACTOR_UUID = UUBluetooth.shortCodeToUuid("2A74");

        /**
         * SpecificationName: Hardware Revision String
         * SpecificationType: org.bluetooth.characteristic.hardware_revision_string
         * AssignedNumber: 0x2A27
         */
        public static final UUID HARDWARE_REVISION_STRING_UUID = UUBluetooth.shortCodeToUuid("2A27");

        /**
         * SpecificationName: Heart Rate Control Point
         * SpecificationType: org.bluetooth.characteristic.heart_rate_control_point
         * AssignedNumber: 0x2A39
         */
        public static final UUID HEART_RATE_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A39");

        /**
         * SpecificationName: Heart Rate Max
         * SpecificationType: org.bluetooth.characteristic.heart_rate_max
         * AssignedNumber: 0x2A8D
         */
        public static final UUID HEART_RATE_MAX_UUID = UUBluetooth.shortCodeToUuid("2A8D");

        /**
         * SpecificationName: Heart Rate Measurement
         * SpecificationType: org.bluetooth.characteristic.heart_rate_measurement
         * AssignedNumber: 0x2A37
         */
        public static final UUID HEART_RATE_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A37");

        /**
         * SpecificationName: Heat Index
         * SpecificationType: org.bluetooth.characteristic.heat_index
         * AssignedNumber: 0x2A7A
         */
        public static final UUID HEAT_INDEX_UUID = UUBluetooth.shortCodeToUuid("2A7A");

        /**
         * SpecificationName: Height
         * SpecificationType: org.bluetooth.characteristic.height
         * AssignedNumber: 0x2A8E
         */
        public static final UUID HEIGHT_UUID = UUBluetooth.shortCodeToUuid("2A8E");

        /**
         * SpecificationName: HID Control Point
         * SpecificationType: org.bluetooth.characteristic.hid_control_point
         * AssignedNumber: 0x2A4C
         */
        public static final UUID HID_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A4C");

        /**
         * SpecificationName: HID Information
         * SpecificationType: org.bluetooth.characteristic.hid_information
         * AssignedNumber: 0x2A4A
         */
        public static final UUID HID_INFORMATION_UUID = UUBluetooth.shortCodeToUuid("2A4A");

        /**
         * SpecificationName: Hip Circumference
         * SpecificationType: org.bluetooth.characteristic.hip_circumference
         * AssignedNumber: 0x2A8F
         */
        public static final UUID HIP_CIRCUMFERENCE_UUID = UUBluetooth.shortCodeToUuid("2A8F");

        /**
         * SpecificationName: HTTP Control Point
         * SpecificationType: org.bluetooth.characteristic.http_control_point
         * AssignedNumber: 0x2ABA
         */
        public static final UUID HTTP_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2ABA");

        /**
         * SpecificationName: HTTP Entity Body
         * SpecificationType: org.bluetooth.characteristic.http_entity_body
         * AssignedNumber: 0x2AB9
         */
        public static final UUID HTTP_ENTITY_BODY_UUID = UUBluetooth.shortCodeToUuid("2AB9");

        /**
         * SpecificationName: HTTP Headers
         * SpecificationType: org.bluetooth.characteristic.http_headers
         * AssignedNumber: 0x2AB7
         */
        public static final UUID HTTP_HEADERS_UUID = UUBluetooth.shortCodeToUuid("2AB7");

        /**
         * SpecificationName: HTTP Status Code
         * SpecificationType: org.bluetooth.characteristic.http_status_code
         * AssignedNumber: 0x2AB8
         */
        public static final UUID HTTP_STATUS_CODE_UUID = UUBluetooth.shortCodeToUuid("2AB8");

        /**
         * SpecificationName: HTTPS Security
         * SpecificationType: org.bluetooth.characteristic.https_security
         * AssignedNumber: 0x2ABB
         */
        public static final UUID HTTPS_SECURITY_UUID = UUBluetooth.shortCodeToUuid("2ABB");

        /**
         * SpecificationName: Humidity
         * SpecificationType: org.bluetooth.characteristic.humidity
         * AssignedNumber: 0x2A6F
         */
        public static final UUID HUMIDITY_UUID = UUBluetooth.shortCodeToUuid("2A6F");

        /**
         * SpecificationName: IEEE 11073-20601 Regulatory Certification Data List
         * SpecificationType: org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list
         * AssignedNumber: 0x2A2A
         */
        public static final UUID IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST_UUID = UUBluetooth.shortCodeToUuid("2A2A");

        /**
         * SpecificationName: Indoor Positioning Configuration
         * SpecificationType: org.bluetooth.characteristic.indoor_positioning_configuration
         * AssignedNumber: 0x2AAD
         */
        public static final UUID INDOOR_POSITIONING_CONFIGURATION_UUID = UUBluetooth.shortCodeToUuid("2AAD");

        /**
         * SpecificationName: Intermediate Cuff Pressure
         * SpecificationType: org.bluetooth.characteristic.intermediate_cuff_pressure
         * AssignedNumber: 0x2A36
         */
        public static final UUID INTERMEDIATE_CUFF_PRESSURE_UUID = UUBluetooth.shortCodeToUuid("2A36");

        /**
         * SpecificationName: Intermediate Temperature
         * SpecificationType: org.bluetooth.characteristic.intermediate_temperature
         * AssignedNumber: 0x2A1E
         */
        public static final UUID INTERMEDIATE_TEMPERATURE_UUID = UUBluetooth.shortCodeToUuid("2A1E");

        /**
         * SpecificationName: Irradiance
         * SpecificationType: org.bluetooth.characteristic.irradiance
         * AssignedNumber: 0x2A77
         */
        public static final UUID IRRADIANCE_UUID = UUBluetooth.shortCodeToUuid("2A77");

        /**
         * SpecificationName: Language
         * SpecificationType: org.bluetooth.characteristic.language
         * AssignedNumber: 0x2AA2
         */
        public static final UUID LANGUAGE_UUID = UUBluetooth.shortCodeToUuid("2AA2");

        /**
         * SpecificationName: Last Name
         * SpecificationType: org.bluetooth.characteristic.last_name
         * AssignedNumber: 0x2A90
         */
        public static final UUID LAST_NAME_UUID = UUBluetooth.shortCodeToUuid("2A90");

        /**
         * SpecificationName: Latitude
         * SpecificationType: org.bluetooth.characteristic.latitude
         * AssignedNumber: 0x2AAE
         */
        public static final UUID LATITUDE_UUID = UUBluetooth.shortCodeToUuid("2AAE");

        /**
         * SpecificationName: LN Control Point
         * SpecificationType: org.bluetooth.characteristic.ln_control_point
         * AssignedNumber: 0x2A6B
         */
        public static final UUID LN_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A6B");

        /**
         * SpecificationName: LN Feature
         * SpecificationType: org.bluetooth.characteristic.ln_feature
         * AssignedNumber: 0x2A6A
         */
        public static final UUID LN_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A6A");

        /**
         * SpecificationName: Local East Coordinate
         * SpecificationType: org.bluetooth.characteristic.local_east_coordinate
         * AssignedNumber: 0x2AB1
         */
        public static final UUID LOCAL_EAST_COORDINATE_UUID = UUBluetooth.shortCodeToUuid("2AB1");

        /**
         * SpecificationName: Local North Coordinate
         * SpecificationType: org.bluetooth.characteristic.local_north_coordinate
         * AssignedNumber: 0x2AB0
         */
        public static final UUID LOCAL_NORTH_COORDINATE_UUID = UUBluetooth.shortCodeToUuid("2AB0");

        /**
         * SpecificationName: Local Time Information
         * SpecificationType: org.bluetooth.characteristic.local_time_information
         * AssignedNumber: 0x2A0F
         */
        public static final UUID LOCAL_TIME_INFORMATION_UUID = UUBluetooth.shortCodeToUuid("2A0F");

        /**
         * SpecificationName: Location and Speed
         * SpecificationType: org.bluetooth.characteristic.location_and_speed
         * AssignedNumber: 0x2A67
         */
        public static final UUID LOCATION_AND_SPEED_UUID = UUBluetooth.shortCodeToUuid("2A67");

        /**
         * SpecificationName: Location Name
         * SpecificationType: org.bluetooth.characteristic.location_name
         * AssignedNumber: 0x2AB5
         */
        public static final UUID LOCATION_NAME_UUID = UUBluetooth.shortCodeToUuid("2AB5");

        /**
         * SpecificationName: Longitude
         * SpecificationType: org.bluetooth.characteristic.longitude
         * AssignedNumber: 0x2AAF
         */
        public static final UUID LONGITUDE_UUID = UUBluetooth.shortCodeToUuid("2AAF");

        /**
         * SpecificationName: Magnetic Declination
         * SpecificationType: org.bluetooth.characteristic.magnetic_declination
         * AssignedNumber: 0x2A2C
         */
        public static final UUID MAGNETIC_DECLINATION_UUID = UUBluetooth.shortCodeToUuid("2A2C");

        /**
         * SpecificationName: Magnetic Flux Density - 2D
         * SpecificationType: org.bluetooth.characteristic.magnetic_flux_density_2D
         * AssignedNumber: 0x2AA0
         */
        public static final UUID MAGNETIC_FLUX_DENSITY_2D_UUID = UUBluetooth.shortCodeToUuid("2AA0");

        /**
         * SpecificationName: Magnetic Flux Density - 3D
         * SpecificationType: org.bluetooth.characteristic.magnetic_flux_density_3D
         * AssignedNumber: 0x2AA1
         */
        public static final UUID MAGNETIC_FLUX_DENSITY_3D_UUID = UUBluetooth.shortCodeToUuid("2AA1");

        /**
         * SpecificationName: Manufacturer Name String
         * SpecificationType: org.bluetooth.characteristic.manufacturer_name_string
         * AssignedNumber: 0x2A29
         */
        public static final UUID MANUFACTURER_NAME_STRING_UUID = UUBluetooth.shortCodeToUuid("2A29");

        /**
         * SpecificationName: Maximum Recommended Heart Rate
         * SpecificationType: org.bluetooth.characteristic.maximum_recommended_heart_rate
         * AssignedNumber: 0x2A91
         */
        public static final UUID MAXIMUM_RECOMMENDED_HEART_RATE_UUID = UUBluetooth.shortCodeToUuid("2A91");

        /**
         * SpecificationName: Measurement Interval
         * SpecificationType: org.bluetooth.characteristic.measurement_interval
         * AssignedNumber: 0x2A21
         */
        public static final UUID MEASUREMENT_INTERVAL_UUID = UUBluetooth.shortCodeToUuid("2A21");

        /**
         * SpecificationName: Model Number String
         * SpecificationType: org.bluetooth.characteristic.model_number_string
         * AssignedNumber: 0x2A24
         */
        public static final UUID MODEL_NUMBER_STRING_UUID = UUBluetooth.shortCodeToUuid("2A24");

        /**
         * SpecificationName: Navigation
         * SpecificationType: org.bluetooth.characteristic.navigation
         * AssignedNumber: 0x2A68
         */
        public static final UUID NAVIGATION_UUID = UUBluetooth.shortCodeToUuid("2A68");

        /**
         * SpecificationName: New Alert
         * SpecificationType: org.bluetooth.characteristic.new_alert
         * AssignedNumber: 0x2A46
         */
        public static final UUID NEW_ALERT_UUID = UUBluetooth.shortCodeToUuid("2A46");

        /**
         * SpecificationName: Object Action Control Point
         * SpecificationType: org.bluetooth.characteristic.object_action_control_point
         * AssignedNumber: 0x2AC5
         */
        public static final UUID OBJECT_ACTION_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2AC5");

        /**
         * SpecificationName: Object Changed
         * SpecificationType: org.bluetooth.characteristic.object_changed
         * AssignedNumber: 0x2AC8
         */
        public static final UUID OBJECT_CHANGED_UUID = UUBluetooth.shortCodeToUuid("2AC8");

        /**
         * SpecificationName: Object First-Created
         * SpecificationType: org.bluetooth.characteristic.object_first_created
         * AssignedNumber: 0x2AC1
         */
        public static final UUID OBJECT_FIRST_CREATED_UUID = UUBluetooth.shortCodeToUuid("2AC1");

        /**
         * SpecificationName: Object ID
         * SpecificationType: org.bluetooth.characteristic.object_id
         * AssignedNumber: 0x2AC3
         */
        public static final UUID OBJECT_ID_UUID = UUBluetooth.shortCodeToUuid("2AC3");

        /**
         * SpecificationName: Object Last-Modified
         * SpecificationType: org.bluetooth.characteristic.object_last_modified
         * AssignedNumber: 0x2AC2
         */
        public static final UUID OBJECT_LAST_MODIFIED_UUID = UUBluetooth.shortCodeToUuid("2AC2");

        /**
         * SpecificationName: Object List Control Point
         * SpecificationType: org.bluetooth.characteristic.object_list_control_point
         * AssignedNumber: 0x2AC6
         */
        public static final UUID OBJECT_LIST_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2AC6");

        /**
         * SpecificationName: Object List Filter
         * SpecificationType: org.bluetooth.characteristic.object_list_filter
         * AssignedNumber: 0x2AC7
         */
        public static final UUID OBJECT_LIST_FILTER_UUID = UUBluetooth.shortCodeToUuid("2AC7");

        /**
         * SpecificationName: Object Name
         * SpecificationType: org.bluetooth.characteristic.object_name
         * AssignedNumber: 0x2ABE
         */
        public static final UUID OBJECT_NAME_UUID = UUBluetooth.shortCodeToUuid("2ABE");

        /**
         * SpecificationName: Object Properties
         * SpecificationType: org.bluetooth.characteristic.object_properties
         * AssignedNumber: 0x2AC4
         */
        public static final UUID OBJECT_PROPERTIES_UUID = UUBluetooth.shortCodeToUuid("2AC4");

        /**
         * SpecificationName: Object Size
         * SpecificationType: org.bluetooth.characteristic.object_size
         * AssignedNumber: 0x2AC0
         */
        public static final UUID OBJECT_SIZE_UUID = UUBluetooth.shortCodeToUuid("2AC0");

        /**
         * SpecificationName: Object Type
         * SpecificationType: org.bluetooth.characteristic.object_type
         * AssignedNumber: 0x2ABF
         */
        public static final UUID OBJECT_TYPE_UUID = UUBluetooth.shortCodeToUuid("2ABF");

        /**
         * SpecificationName: OTS Feature
         * SpecificationType: org.bluetooth.characteristic.ots_feature
         * AssignedNumber: 0x2ABD
         */
        public static final UUID OTS_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2ABD");

        /**
         * SpecificationName: Peripheral Preferred Connection Parameters
         * SpecificationType: org.bluetooth.characteristic.gap.peripheral_preferred_connection_parameters
         * AssignedNumber: 0x2A04
         */
        public static final UUID PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID = UUBluetooth.shortCodeToUuid("2A04");

        /**
         * SpecificationName: Peripheral Privacy Flag
         * SpecificationType: org.bluetooth.characteristic.gap.peripheral_privacy_flag
         * AssignedNumber: 0x2A02
         */
        public static final UUID PERIPHERAL_PRIVACY_FLAG_UUID = UUBluetooth.shortCodeToUuid("2A02");

        /**
         * SpecificationName: PLX Continuous Measurement
         * SpecificationType: org.bluetooth.characteristic.plx_continuous_measurement
         * AssignedNumber: 0x2A5F
         */
        public static final UUID PLX_CONTINUOUS_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A5F");

        /**
         * SpecificationName: PLX Features
         * SpecificationType: org.bluetooth.characteristic.plx_features
         * AssignedNumber: 0x2A60
         */
        public static final UUID PLX_FEATURES_UUID = UUBluetooth.shortCodeToUuid("2A60");

        /**
         * SpecificationName: PLX Spot-Check Measurement
         * SpecificationType: org.bluetooth.characteristic.plx_spot_check_measurement
         * AssignedNumber: 0x2A5E
         */
        public static final UUID PLX_SPOT_CHECK_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A5E");

        /**
         * SpecificationName: PnP ID
         * SpecificationType: org.bluetooth.characteristic.pnp_id
         * AssignedNumber: 0x2A50
         */
        public static final UUID PNP_ID_UUID = UUBluetooth.shortCodeToUuid("2A50");

        /**
         * SpecificationName: Pollen Concentration
         * SpecificationType: org.bluetooth.characteristic.pollen_concentration
         * AssignedNumber: 0x2A75
         */
        public static final UUID POLLEN_CONCENTRATION_UUID = UUBluetooth.shortCodeToUuid("2A75");

        /**
         * SpecificationName: Position Quality
         * SpecificationType: org.bluetooth.characteristic.position_quality
         * AssignedNumber: 0x2A69
         */
        public static final UUID POSITION_QUALITY_UUID = UUBluetooth.shortCodeToUuid("2A69");

        /**
         * SpecificationName: Pressure
         * SpecificationType: org.bluetooth.characteristic.pressure
         * AssignedNumber: 0x2A6D
         */
        public static final UUID PRESSURE_UUID = UUBluetooth.shortCodeToUuid("2A6D");

        /**
         * SpecificationName: Protocol Mode
         * SpecificationType: org.bluetooth.characteristic.protocol_mode
         * AssignedNumber: 0x2A4E
         */
        public static final UUID PROTOCOL_MODE_UUID = UUBluetooth.shortCodeToUuid("2A4E");

        /**
         * SpecificationName: Rainfall
         * SpecificationType: org.bluetooth.characteristic.rainfall
         * AssignedNumber: 0x2A78
         */
        public static final UUID RAINFALL_UUID = UUBluetooth.shortCodeToUuid("2A78");

        /**
         * SpecificationName: Reconnection Address
         * SpecificationType: org.bluetooth.characteristic.gap.reconnection_address
         * AssignedNumber: 0x2A03
         */
        public static final UUID RECONNECTION_ADDRESS_UUID = UUBluetooth.shortCodeToUuid("2A03");

        /**
         * SpecificationName: Record Access Control Point
         * SpecificationType: org.bluetooth.characteristic.record_access_control_point
         * AssignedNumber: 0x2A52
         */
        public static final UUID RECORD_ACCESS_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A52");

        /**
         * SpecificationName: Reference Time Information
         * SpecificationType: org.bluetooth.characteristic.reference_time_information
         * AssignedNumber: 0x2A14
         */
        public static final UUID REFERENCE_TIME_INFORMATION_UUID = UUBluetooth.shortCodeToUuid("2A14");

        /**
         * SpecificationName: Report
         * SpecificationType: org.bluetooth.characteristic.report
         * AssignedNumber: 0x2A4D
         */
        public static final UUID REPORT_UUID = UUBluetooth.shortCodeToUuid("2A4D");

        /**
         * SpecificationName: Report Map
         * SpecificationType: org.bluetooth.characteristic.report_map
         * AssignedNumber: 0x2A4B
         */
        public static final UUID REPORT_MAP_UUID = UUBluetooth.shortCodeToUuid("2A4B");

        /**
         * SpecificationName: Resolvable Private Address Only
         * SpecificationType: org.bluetooth.characteristic.resolvable_private_address_only
         * AssignedNumber: 2AC9
         */
        public static final UUID RESOLVABLE_PRIVATE_ADDRESS_ONLY_UUID = UUBluetooth.shortCodeToUuid("2AC9");

        /**
         * SpecificationName: Resting Heart Rate
         * SpecificationType: org.bluetooth.characteristic.resting_heart_rate
         * AssignedNumber: 0x2A92
         */
        public static final UUID RESTING_HEART_RATE_UUID = UUBluetooth.shortCodeToUuid("2A92");

        /**
         * SpecificationName: Ringer Control Point
         * SpecificationType: org.bluetooth.characteristic.ringer_control_point
         * AssignedNumber: 0x2A40
         */
        public static final UUID RINGER_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A40");

        /**
         * SpecificationName: Ringer Setting
         * SpecificationType: org.bluetooth.characteristic.ringer_setting
         * AssignedNumber: 0x2A41
         */
        public static final UUID RINGER_SETTING_UUID = UUBluetooth.shortCodeToUuid("2A41");

        /**
         * SpecificationName: RSC Feature
         * SpecificationType: org.bluetooth.characteristic.rsc_feature
         * AssignedNumber: 0x2A54
         */
        public static final UUID RSC_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A54");

        /**
         * SpecificationName: RSC Measurement
         * SpecificationType: org.bluetooth.characteristic.rsc_measurement
         * AssignedNumber: 0x2A53
         */
        public static final UUID RSC_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A53");

        /**
         * SpecificationName: SC Control Point
         * SpecificationType: org.bluetooth.characteristic.sc_control_point
         * AssignedNumber: 0x2A55
         */
        public static final UUID SC_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A55");

        /**
         * SpecificationName: Scan Interval Window
         * SpecificationType: org.bluetooth.characteristic.scan_interval_window
         * AssignedNumber: 0x2A4F
         */
        public static final UUID SCAN_INTERVAL_WINDOW_UUID = UUBluetooth.shortCodeToUuid("2A4F");

        /**
         * SpecificationName: Scan Refresh
         * SpecificationType: org.bluetooth.characteristic.scan_refresh
         * AssignedNumber: 0x2A31
         */
        public static final UUID SCAN_REFRESH_UUID = UUBluetooth.shortCodeToUuid("2A31");

        /**
         * SpecificationName: Sensor Location
         * SpecificationType: org.blueooth.characteristic.sensor_location
         * AssignedNumber: 0x2A5D
         */
        public static final UUID SENSOR_LOCATION_UUID = UUBluetooth.shortCodeToUuid("2A5D");

        /**
         * SpecificationName: Serial Number String
         * SpecificationType: org.bluetooth.characteristic.serial_number_string
         * AssignedNumber: 0x2A25
         */
        public static final UUID SERIAL_NUMBER_STRING_UUID = UUBluetooth.shortCodeToUuid("2A25");

        /**
         * SpecificationName: Service Changed
         * SpecificationType: org.bluetooth.characteristic.gatt.service_changed
         * AssignedNumber: 0x2A05
         */
        public static final UUID SERVICE_CHANGED_UUID = UUBluetooth.shortCodeToUuid("2A05");

        /**
         * SpecificationName: Software Revision String
         * SpecificationType: org.bluetooth.characteristic.software_revision_string
         * AssignedNumber: 0x2A28
         */
        public static final UUID SOFTWARE_REVISION_STRING_UUID = UUBluetooth.shortCodeToUuid("2A28");

        /**
         * SpecificationName: Sport Type for Aerobic and Anaerobic Thresholds
         * SpecificationType: org.bluetooth.characteristic.sport_type_for_aerobic_and_anaerobic_thresholds
         * AssignedNumber: 0x2A93
         */
        public static final UUID SPORT_TYPE_FOR_AEROBIC_AND_ANAEROBIC_THRESHOLDS_UUID = UUBluetooth.shortCodeToUuid("2A93");

        /**
         * SpecificationName: Supported New Alert Category
         * SpecificationType: org.bluetooth.characteristic.supported_new_alert_category
         * AssignedNumber: 0x2A47
         */
        public static final UUID SUPPORTED_NEW_ALERT_CATEGORY_UUID = UUBluetooth.shortCodeToUuid("2A47");

        /**
         * SpecificationName: Supported Unread Alert Category
         * SpecificationType: org.bluetooth.characteristic.supported_unread_alert_category
         * AssignedNumber: 0x2A48
         */
        public static final UUID SUPPORTED_UNREAD_ALERT_CATEGORY_UUID = UUBluetooth.shortCodeToUuid("2A48");

        /**
         * SpecificationName: System ID
         * SpecificationType: org.bluetooth.characteristic.system_id
         * AssignedNumber: 0x2A23
         */
        public static final UUID SYSTEM_ID_UUID = UUBluetooth.shortCodeToUuid("2A23");

        /**
         * SpecificationName: TDS Control Point
         * SpecificationType: org.bluetooth.characteristic.tds_control_point
         * AssignedNumber: 0x2ABC
         */
        public static final UUID TDS_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2ABC");

        /**
         * SpecificationName: Temperature
         * SpecificationType: org.bluetooth.characteristic.temperature
         * AssignedNumber: 0x2A6E
         */
        public static final UUID TEMPERATURE_UUID = UUBluetooth.shortCodeToUuid("2A6E");

        /**
         * SpecificationName: Temperature Measurement
         * SpecificationType: org.bluetooth.characteristic.temperature_measurement
         * AssignedNumber: 0x2A1C
         */
        public static final UUID TEMPERATURE_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A1C");

        /**
         * SpecificationName: Temperature Type
         * SpecificationType: org.bluetooth.characteristic.temperature_type
         * AssignedNumber: 0x2A1D
         */
        public static final UUID TEMPERATURE_TYPE_UUID = UUBluetooth.shortCodeToUuid("2A1D");

        /**
         * SpecificationName: Three Zone Heart Rate Limits
         * SpecificationType: org.bluetooth.characteristic.three_zone_heart_rate_limits
         * AssignedNumber: 0x2A94
         */
        public static final UUID THREE_ZONE_HEART_RATE_LIMITS_UUID = UUBluetooth.shortCodeToUuid("2A94");

        /**
         * SpecificationName: Time Accuracy
         * SpecificationType: org.bluetooth.characteristic.time_accuracy
         * AssignedNumber: 0x2A12
         */
        public static final UUID TIME_ACCURACY_UUID = UUBluetooth.shortCodeToUuid("2A12");

        /**
         * SpecificationName: Time Source
         * SpecificationType: org.bluetooth.characteristic.time_source
         * AssignedNumber: 0x2A13
         */
        public static final UUID TIME_SOURCE_UUID = UUBluetooth.shortCodeToUuid("2A13");

        /**
         * SpecificationName: Time Update Control Point
         * SpecificationType: org.bluetooth.characteristic.time_update_control_point
         * AssignedNumber: 0x2A16
         */
        public static final UUID TIME_UPDATE_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A16");

        /**
         * SpecificationName: Time Update State
         * SpecificationType: org.bluetooth.characteristic.time_update_state
         * AssignedNumber: 0x2A17
         */
        public static final UUID TIME_UPDATE_STATE_UUID = UUBluetooth.shortCodeToUuid("2A17");

        /**
         * SpecificationName: Time with DST
         * SpecificationType: org.bluetooth.characteristic.time_with_dst
         * AssignedNumber: 0x2A11
         */
        public static final UUID TIME_WITH_DST_UUID = UUBluetooth.shortCodeToUuid("2A11");

        /**
         * SpecificationName: Time Zone
         * SpecificationType: org.bluetooth.characteristic.time_zone
         * AssignedNumber: 0x2A0E
         */
        public static final UUID TIME_ZONE_UUID = UUBluetooth.shortCodeToUuid("2A0E");

        /**
         * SpecificationName: True Wind Direction
         * SpecificationType: org.bluetooth.characteristic.true_wind_direction
         * AssignedNumber: 0x2A71
         */
        public static final UUID TRUE_WIND_DIRECTION_UUID = UUBluetooth.shortCodeToUuid("2A71");

        /**
         * SpecificationName: True Wind Speed
         * SpecificationType: org.bluetooth.characteristic.true_wind_speed
         * AssignedNumber: 0x2A70
         */
        public static final UUID TRUE_WIND_SPEED_UUID = UUBluetooth.shortCodeToUuid("2A70");

        /**
         * SpecificationName: Two Zone Heart Rate Limit
         * SpecificationType: org.bluetooth.characteristic.two_zone_heart_rate_limit
         * AssignedNumber: 0x2A95
         */
        public static final UUID TWO_ZONE_HEART_RATE_LIMIT_UUID = UUBluetooth.shortCodeToUuid("2A95");

        /**
         * SpecificationName: Tx Power Level
         * SpecificationType: org.bluetooth.characteristic.tx_power_level
         * AssignedNumber: 0x2A07
         */
        public static final UUID TX_POWER_LEVEL_UUID = UUBluetooth.shortCodeToUuid("2A07");

        /**
         * SpecificationName: Uncertainty
         * SpecificationType: org.bluetooth.characteristic.uncertainty
         * AssignedNumber: 0x2AB4
         */
        public static final UUID UNCERTAINTY_UUID = UUBluetooth.shortCodeToUuid("2AB4");

        /**
         * SpecificationName: Unread Alert Status
         * SpecificationType: org.bluetooth.characteristic.unread_alert_status
         * AssignedNumber: 0x2A45
         */
        public static final UUID UNREAD_ALERT_STATUS_UUID = UUBluetooth.shortCodeToUuid("2A45");

        /**
         * SpecificationName: URI
         * SpecificationType: org.bluetooth.characteristic.uri
         * AssignedNumber: 0x2AB6
         */
        public static final UUID URI_UUID = UUBluetooth.shortCodeToUuid("2AB6");

        /**
         * SpecificationName: User Control Point
         * SpecificationType: org.bluetooth.characteristic.user_control_point
         * AssignedNumber: 0x2A9F
         */
        public static final UUID USER_CONTROL_POINT_UUID = UUBluetooth.shortCodeToUuid("2A9F");

        /**
         * SpecificationName: User Index
         * SpecificationType: org.bluetooth.characteristic.user_index
         * AssignedNumber: 0x2A9A
         */
        public static final UUID USER_INDEX_UUID = UUBluetooth.shortCodeToUuid("2A9A");

        /**
         * SpecificationName: UV Index
         * SpecificationType: org.bluetooth.characteristic.uv_index
         * AssignedNumber: 0x2A76
         */
        public static final UUID UV_INDEX_UUID = UUBluetooth.shortCodeToUuid("2A76");

        /**
         * SpecificationName: VO2 Max
         * SpecificationType: org.bluetooth.characteristic.vo2_max
         * AssignedNumber: 0x2A96
         */
        public static final UUID VO2_MAX_UUID = UUBluetooth.shortCodeToUuid("2A96");

        /**
         * SpecificationName: Waist Circumference
         * SpecificationType: org.bluetooth.characteristic.waist_circumference
         * AssignedNumber: 0x2A97
         */
        public static final UUID WAIST_CIRCUMFERENCE_UUID = UUBluetooth.shortCodeToUuid("2A97");

        /**
         * SpecificationName: Weight
         * SpecificationType: org.bluetooth.characteristic.weight
         * AssignedNumber: 0x2A98
         */
        public static final UUID WEIGHT_UUID = UUBluetooth.shortCodeToUuid("2A98");

        /**
         * SpecificationName: Weight Measurement
         * SpecificationType: org.bluetooth.characteristic.weight_measurement
         * AssignedNumber: 0x2A9D
         */
        public static final UUID WEIGHT_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("2A9D");

        /**
         * SpecificationName: Weight Scale Feature
         * SpecificationType: org.bluetooth.characteristic.weight_scale_feature
         * AssignedNumber: 0x2A9E
         */
        public static final UUID WEIGHT_SCALE_FEATURE_UUID = UUBluetooth.shortCodeToUuid("2A9E");

        /**
         * SpecificationName: Wind Chill
         * SpecificationType: org.bluetooth.characteristic.wind_chill
         * AssignedNumber: 0x2A79
         */
        public static final UUID WIND_CHILL_UUID = UUBluetooth.shortCodeToUuid("2A79");
    }

    public static class Descriptors
    {
        /**
         * SpecificationName: Characteristic Aggregate Format
         * SpecificationType: org.bluetooth.descriptor.gatt.characteristic_aggregate_format
         * AssignedNumber: 0x2905
         */
        public static final UUID CHARACTERISTIC_AGGREGATE_FORMAT_UUID = UUBluetooth.shortCodeToUuid("2905");

        /**
         * SpecificationName: Characteristic Extended Properties
         * SpecificationType: org.bluetooth.descriptor.gatt.characteristic_extended_properties
         * AssignedNumber: 0x2900
         */
        public static final UUID CHARACTERISTIC_EXTENDED_PROPERTIES_UUID = UUBluetooth.shortCodeToUuid("2900");

        /**
         * SpecificationName: Characteristic Presentation Format
         * SpecificationType: org.bluetooth.descriptor.gatt.characteristic_presentation_format
         * AssignedNumber: 0x2904
         */
        public static final UUID CHARACTERISTIC_PRESENTATION_FORMAT_UUID = UUBluetooth.shortCodeToUuid("2904");

        /**
         * SpecificationName: Characteristic User Description
         * SpecificationType: org.bluetooth.descriptor.gatt.characteristic_user_description
         * AssignedNumber: 0x2901
         */
        public static final UUID CHARACTERISTIC_USER_DESCRIPTION_UUID = UUBluetooth.shortCodeToUuid("2901");

        /**
         * SpecificationName: Client Characteristic Configuration
         * SpecificationType: org.bluetooth.descriptor.gatt.client_characteristic_configuration
         * AssignedNumber: 0x2902
         */
        public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUBluetooth.shortCodeToUuid("2902");

        /**
         * SpecificationName: Environmental Sensing Configuration
         * SpecificationType: org.bluetooth.descriptor.es_configuration
         * AssignedNumber: 0x290B
         */
        public static final UUID ENVIRONMENTAL_SENSING_CONFIGURATION_UUID = UUBluetooth.shortCodeToUuid("290B");

        /**
         * SpecificationName: Environmental Sensing Measurement
         * SpecificationType: org.bluetooth.descriptor.es_measurement
         * AssignedNumber: 0x290C
         */
        public static final UUID ENVIRONMENTAL_SENSING_MEASUREMENT_UUID = UUBluetooth.shortCodeToUuid("290C");

        /**
         * SpecificationName: Environmental Sensing Trigger Setting
         * SpecificationType: org.bluetooth.descriptor.es_trigger_setting
         * AssignedNumber: 0x290D
         */
        public static final UUID ENVIRONMENTAL_SENSING_TRIGGER_SETTING_UUID = UUBluetooth.shortCodeToUuid("290D");

        /**
         * SpecificationName: External Report Reference
         * SpecificationType: org.bluetooth.descriptor.external_report_reference
         * AssignedNumber: 0x2907
         */
        public static final UUID EXTERNAL_REPORT_REFERENCE_UUID = UUBluetooth.shortCodeToUuid("2907");

        /**
         * SpecificationName: Number of Digitals
         * SpecificationType: org.bluetooth.descriptor.number_of_digitals
         * AssignedNumber: 0x2909
         */
        public static final UUID NUMBER_OF_DIGITALS_UUID = UUBluetooth.shortCodeToUuid("2909");

        /**
         * SpecificationName: Report Reference
         * SpecificationType: org.bluetooth.descriptor.report_reference
         * AssignedNumber: 0x2908
         */
        public static final UUID REPORT_REFERENCE_UUID = UUBluetooth.shortCodeToUuid("2908");

        /**
         * SpecificationName: Server Characteristic Configuration
         * SpecificationType: org.bluetooth.descriptor.gatt.server_characteristic_configuration
         * AssignedNumber: 0x2903
         */
        public static final UUID SERVER_CHARACTERISTIC_CONFIGURATION_UUID = UUBluetooth.shortCodeToUuid("2903");

        /**
         * SpecificationName: Time Trigger Setting
         * SpecificationType: org.bluetooth.descriptor.time_trigger_setting
         * AssignedNumber: 0x290E
         */
        public static final UUID TIME_TRIGGER_SETTING_UUID = UUBluetooth.shortCodeToUuid("290E");

        /**
         * SpecificationName: Valid Range
         * SpecificationType: org.bluetooth.descriptor.valid_range
         * AssignedNumber: 0x2906
         */
        public static final UUID VALID_RANGE_UUID = UUBluetooth.shortCodeToUuid("2906");

        /**
         * SpecificationName: Value Trigger Setting
         * SpecificationType: org.bluetooth.descriptor.value_trigger_setting
         * AssignedNumber: 0x290A
         */
        public static final UUID VALUE_TRIGGER_SETTING_UUID = UUBluetooth.shortCodeToUuid("290A");
    }

    static final HashMap<UUID, String> BLUETOOTH_SPEC_NAMES = new HashMap<>();

    static
    {
        BLUETOOTH_SPEC_NAMES.put(Services.SERIAL_PORT_PROFILE_UUID, "Serial Port Profile");
        BLUETOOTH_SPEC_NAMES.put(Services.ALERT_NOTIFICATION_SERVICE_UUID, "Alert Notification Service");
        BLUETOOTH_SPEC_NAMES.put(Services.AUTOMATION_IO_UUID, "Automation IO");
        BLUETOOTH_SPEC_NAMES.put(Services.BATTERY_SERVICE_UUID, "Battery Service");
        BLUETOOTH_SPEC_NAMES.put(Services.BLOOD_PRESSURE_UUID, "Blood Pressure");
        BLUETOOTH_SPEC_NAMES.put(Services.BODY_COMPOSITION_UUID, "Body Composition");
        BLUETOOTH_SPEC_NAMES.put(Services.BOND_MANAGEMENT_UUID, "Bond Management");
        BLUETOOTH_SPEC_NAMES.put(Services.CONTINUOUS_GLUCOSE_MONITORING_UUID, "Continuous Glucose Monitoring");
        BLUETOOTH_SPEC_NAMES.put(Services.CURRENT_TIME_SERVICE_UUID, "Current Time Service");
        BLUETOOTH_SPEC_NAMES.put(Services.CYCLING_POWER_UUID, "Cycling Power");
        BLUETOOTH_SPEC_NAMES.put(Services.CYCLING_SPEED_AND_CADENCE_UUID, "Cycling Speed and Cadence");
        BLUETOOTH_SPEC_NAMES.put(Services.DEVICE_INFORMATION_UUID, "Device Information");
        BLUETOOTH_SPEC_NAMES.put(Services.ENVIRONMENTAL_SENSING_UUID, "Environmental Sensing");
        BLUETOOTH_SPEC_NAMES.put(Services.GENERIC_ACCESS_UUID, "Generic Access");
        BLUETOOTH_SPEC_NAMES.put(Services.GENERIC_ATTRIBUTE_UUID, "Generic Attribute");
        BLUETOOTH_SPEC_NAMES.put(Services.GLUCOSE_UUID, "Glucose");
        BLUETOOTH_SPEC_NAMES.put(Services.HEALTH_THERMOMETER_UUID, "Health Thermometer");
        BLUETOOTH_SPEC_NAMES.put(Services.HEART_RATE_UUID, "Heart Rate");
        BLUETOOTH_SPEC_NAMES.put(Services.HTTP_PROXY_UUID, "HTTP Proxy");
        BLUETOOTH_SPEC_NAMES.put(Services.HUMAN_INTERFACE_DEVICE_UUID, "Human Interface Device");
        BLUETOOTH_SPEC_NAMES.put(Services.IMMEDIATE_ALERT_UUID, "Immediate Alert");
        BLUETOOTH_SPEC_NAMES.put(Services.INDOOR_POSITIONING_UUID, "Indoor Positioning");
        BLUETOOTH_SPEC_NAMES.put(Services.INTERNET_PROTOCOL_SUPPORT_UUID, "Internet Protocol Support");
        BLUETOOTH_SPEC_NAMES.put(Services.LINK_LOSS_UUID, "Link Loss");
        BLUETOOTH_SPEC_NAMES.put(Services.LOCATION_AND_NAVIGATION_UUID, "Location and Navigation");
        BLUETOOTH_SPEC_NAMES.put(Services.NEXT_DST_CHANGE_SERVICE_UUID, "Next DST Change Service");
        BLUETOOTH_SPEC_NAMES.put(Services.OBJECT_TRANSFER_UUID, "Object Transfer");
        BLUETOOTH_SPEC_NAMES.put(Services.PHONE_ALERT_STATUS_SERVICE_UUID, "Phone Alert Status Service");
        BLUETOOTH_SPEC_NAMES.put(Services.PULSE_OXIMETER_UUID, "Pulse Oximeter");
        BLUETOOTH_SPEC_NAMES.put(Services.REFERENCE_TIME_UPDATE_SERVICE_UUID, "Reference Time Update Service");
        BLUETOOTH_SPEC_NAMES.put(Services.RUNNING_SPEED_AND_CADENCE_UUID, "Running Speed and Cadence");
        BLUETOOTH_SPEC_NAMES.put(Services.SCAN_PARAMETERS_UUID, "Scan Parameters");
        BLUETOOTH_SPEC_NAMES.put(Services.TRANSPORT_DISCOVERY_UUID, "Transport Discovery");
        BLUETOOTH_SPEC_NAMES.put(Services.TX_POWER_UUID, "Tx Power");
        BLUETOOTH_SPEC_NAMES.put(Services.USER_DATA_UUID, "User Data");
        BLUETOOTH_SPEC_NAMES.put(Services.WEIGHT_SCALE_UUID, "Weight Scale");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.AEROBIC_HEART_RATE_LOWER_LIMIT_UUID, "Aerobic Heart Rate Lower Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.AEROBIC_HEART_RATE_UPPER_LIMIT_UUID, "Aerobic Heart Rate Upper Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.AEROBIC_THRESHOLD_UUID, "Aerobic Threshold");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.AGE_UUID, "Age");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.AGGREGATE_UUID, "Aggregate");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ALERT_CATEGORY_ID_UUID, "Alert Category ID");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ALERT_CATEGORY_ID_BIT_MASK_UUID, "Alert Category ID Bit Mask");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ALERT_LEVEL_UUID, "Alert Level");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ALERT_NOTIFICATION_CONTROL_POINT_UUID, "Alert Notification Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ALERT_STATUS_UUID, "Alert Status");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ALTITUDE_UUID, "Altitude");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ANAEROBIC_HEART_RATE_LOWER_LIMIT_UUID, "Anaerobic Heart Rate Lower Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ANAEROBIC_HEART_RATE_UPPER_LIMIT_UUID, "Anaerobic Heart Rate Upper Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ANAEROBIC_THRESHOLD_UUID, "Anaerobic Threshold");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ANALOG_UUID, "Analog");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.APPARENT_WIND_DIRECTION_UUID, "Apparent Wind Direction");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.APPARENT_WIND_SPEED_UUID, "Apparent Wind Speed");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.APPEARANCE_UUID, "Appearance");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BAROMETRIC_PRESSURE_TREND_UUID, "Barometric Pressure Trend");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BATTERY_LEVEL_UUID, "Battery Level");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BLOOD_PRESSURE_FEATURE_UUID, "Blood Pressure Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BLOOD_PRESSURE_MEASUREMENT_UUID, "Blood Pressure Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BODY_COMPOSITION_FEATURE_UUID, "Body Composition Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BODY_COMPOSITION_MEASUREMENT_UUID, "Body Composition Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BODY_SENSOR_LOCATION_UUID, "Body Sensor Location");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BOND_MANAGEMENT_CONTROL_POINT_UUID, "Bond Management Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BOND_MANAGEMENT_FEATURE_UUID, "Bond Management Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BOOT_KEYBOARD_INPUT_REPORT_UUID, "Boot Keyboard Input Report");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BOOT_KEYBOARD_OUTPUT_REPORT_UUID, "Boot Keyboard Output Report");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.BOOT_MOUSE_INPUT_REPORT_UUID, "Boot Mouse Input Report");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CENTRAL_ADDRESS_RESOLUTION_UUID, "Central Address Resolution");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CGM_FEATURE_UUID, "CGM Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CGM_MEASUREMENT_UUID, "CGM Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CGM_SESSION_RUN_TIME_UUID, "CGM Session Run Time");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CGM_SESSION_START_TIME_UUID, "CGM Session Start Time");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CGM_SPECIFIC_OPS_CONTROL_POINT_UUID, "CGM Specific Ops Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CGM_STATUS_UUID, "CGM Status");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CSC_FEATURE_UUID, "CSC Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CSC_MEASUREMENT_UUID, "CSC Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CURRENT_TIME_UUID, "Current Time");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CYCLING_POWER_CONTROL_POINT_UUID, "Cycling Power Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CYCLING_POWER_FEATURE_UUID, "Cycling Power Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CYCLING_POWER_MEASUREMENT_UUID, "Cycling Power Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.CYCLING_POWER_VECTOR_UUID, "Cycling Power Vector");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DATABASE_CHANGE_INCREMENT_UUID, "Database Change Increment");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DATE_OF_BIRTH_UUID, "Date of Birth");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DATE_OF_THRESHOLD_ASSESSMENT_UUID, "Date of Threshold Assessment");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DATE_TIME_UUID, "Date Time");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DAY_DATE_TIME_UUID, "Day Date Time");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DAY_OF_WEEK_UUID, "Day of Week");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DESCRIPTOR_VALUE_CHANGED_UUID, "Descriptor Value Changed");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DEVICE_NAME_UUID, "Device Name");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DEW_POINT_UUID, "Dew Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DIGITAL_UUID, "Digital");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.DST_OFFSET_UUID, "DST Offset");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.ELEVATION_UUID, "Elevation");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.EMAIL_ADDRESS_UUID, "Email Address");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.EXACT_TIME_256_UUID, "Exact Time 256");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.FAT_BURN_HEART_RATE_LOWER_LIMIT_UUID, "Fat Burn Heart Rate Lower Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.FAT_BURN_HEART_RATE_UPPER_LIMIT_UUID, "Fat Burn Heart Rate Upper Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.FIRMWARE_REVISION_STRING_UUID, "Firmware Revision String");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.FIRST_NAME_UUID, "First Name");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.FIVE_ZONE_HEART_RATE_LIMITS_UUID, "Five Zone Heart Rate Limits");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.FLOOR_NUMBER_UUID, "Floor Number");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.GENDER_UUID, "Gender");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.GLUCOSE_FEATURE_UUID, "Glucose Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.GLUCOSE_MEASUREMENT_UUID, "Glucose Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.GLUCOSE_MEASUREMENT_CONTEXT_UUID, "Glucose Measurement Context");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.GUST_FACTOR_UUID, "Gust Factor");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HARDWARE_REVISION_STRING_UUID, "Hardware Revision String");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HEART_RATE_CONTROL_POINT_UUID, "Heart Rate Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HEART_RATE_MAX_UUID, "Heart Rate Max");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HEART_RATE_MEASUREMENT_UUID, "Heart Rate Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HEAT_INDEX_UUID, "Heat Index");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HEIGHT_UUID, "Height");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HID_CONTROL_POINT_UUID, "HID Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HID_INFORMATION_UUID, "HID Information");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HIP_CIRCUMFERENCE_UUID, "Hip Circumference");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HTTP_CONTROL_POINT_UUID, "HTTP Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HTTP_ENTITY_BODY_UUID, "HTTP Entity Body");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HTTP_HEADERS_UUID, "HTTP Headers");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HTTP_STATUS_CODE_UUID, "HTTP Status Code");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HTTPS_SECURITY_UUID, "HTTPS Security");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.HUMIDITY_UUID, "Humidity");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST_UUID, "IEEE 11073-20601 Regulatory Certification Data List");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.INDOOR_POSITIONING_CONFIGURATION_UUID, "Indoor Positioning Configuration");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.INTERMEDIATE_CUFF_PRESSURE_UUID, "Intermediate Cuff Pressure");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.INTERMEDIATE_TEMPERATURE_UUID, "Intermediate Temperature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.IRRADIANCE_UUID, "Irradiance");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LANGUAGE_UUID, "Language");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LAST_NAME_UUID, "Last Name");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LATITUDE_UUID, "Latitude");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LN_CONTROL_POINT_UUID, "LN Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LN_FEATURE_UUID, "LN Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LOCAL_EAST_COORDINATE_UUID, "Local East Coordinate");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LOCAL_NORTH_COORDINATE_UUID, "Local North Coordinate");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LOCAL_TIME_INFORMATION_UUID, "Local Time Information");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LOCATION_AND_SPEED_UUID, "Location and Speed");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LOCATION_NAME_UUID, "Location Name");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.LONGITUDE_UUID, "Longitude");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MAGNETIC_DECLINATION_UUID, "Magnetic Declination");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MAGNETIC_FLUX_DENSITY_2D_UUID, "Magnetic Flux Density - 2D");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MAGNETIC_FLUX_DENSITY_3D_UUID, "Magnetic Flux Density - 3D");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MANUFACTURER_NAME_STRING_UUID, "Manufacturer Name String");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MAXIMUM_RECOMMENDED_HEART_RATE_UUID, "Maximum Recommended Heart Rate");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MEASUREMENT_INTERVAL_UUID, "Measurement Interval");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.MODEL_NUMBER_STRING_UUID, "Model Number String");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.NAVIGATION_UUID, "Navigation");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.NEW_ALERT_UUID, "New Alert");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_ACTION_CONTROL_POINT_UUID, "Object Action Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_CHANGED_UUID, "Object Changed");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_FIRST_CREATED_UUID, "Object First-Created");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_ID_UUID, "Object ID");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_LAST_MODIFIED_UUID, "Object Last-Modified");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_LIST_CONTROL_POINT_UUID, "Object List Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_LIST_FILTER_UUID, "Object List Filter");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_NAME_UUID, "Object Name");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_PROPERTIES_UUID, "Object Properties");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_SIZE_UUID, "Object Size");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OBJECT_TYPE_UUID, "Object Type");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.OTS_FEATURE_UUID, "OTS Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_UUID, "Peripheral Preferred Connection Parameters");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PERIPHERAL_PRIVACY_FLAG_UUID, "Peripheral Privacy Flag");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PLX_CONTINUOUS_MEASUREMENT_UUID, "PLX Continuous Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PLX_FEATURES_UUID, "PLX Features");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PLX_SPOT_CHECK_MEASUREMENT_UUID, "PLX Spot-Check Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PNP_ID_UUID, "PnP ID");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.POLLEN_CONCENTRATION_UUID, "Pollen Concentration");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.POSITION_QUALITY_UUID, "Position Quality");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PRESSURE_UUID, "Pressure");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.PROTOCOL_MODE_UUID, "Protocol Mode");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RAINFALL_UUID, "Rainfall");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RECONNECTION_ADDRESS_UUID, "Reconnection Address");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RECORD_ACCESS_CONTROL_POINT_UUID, "Record Access Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.REFERENCE_TIME_INFORMATION_UUID, "Reference Time Information");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.REPORT_UUID, "Report");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.REPORT_MAP_UUID, "Report Map");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RESOLVABLE_PRIVATE_ADDRESS_ONLY_UUID, "Resolvable Private Address Only");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RESTING_HEART_RATE_UUID, "Resting Heart Rate");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RINGER_CONTROL_POINT_UUID, "Ringer Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RINGER_SETTING_UUID, "Ringer Setting");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RSC_FEATURE_UUID, "RSC Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.RSC_MEASUREMENT_UUID, "RSC Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SC_CONTROL_POINT_UUID, "SC Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SCAN_INTERVAL_WINDOW_UUID, "Scan Interval Window");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SCAN_REFRESH_UUID, "Scan Refresh");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SENSOR_LOCATION_UUID, "Sensor Location");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SERIAL_NUMBER_STRING_UUID, "Serial Number String");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SERVICE_CHANGED_UUID, "Service Changed");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SOFTWARE_REVISION_STRING_UUID, "Software Revision String");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SPORT_TYPE_FOR_AEROBIC_AND_ANAEROBIC_THRESHOLDS_UUID, "Sport Type for Aerobic and Anaerobic Thresholds");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SUPPORTED_NEW_ALERT_CATEGORY_UUID, "Supported New Alert Category");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SUPPORTED_UNREAD_ALERT_CATEGORY_UUID, "Supported Unread Alert Category");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.SYSTEM_ID_UUID, "System ID");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TDS_CONTROL_POINT_UUID, "TDS Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TEMPERATURE_UUID, "Temperature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TEMPERATURE_MEASUREMENT_UUID, "Temperature Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TEMPERATURE_TYPE_UUID, "Temperature Type");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.THREE_ZONE_HEART_RATE_LIMITS_UUID, "Three Zone Heart Rate Limits");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TIME_ACCURACY_UUID, "Time Accuracy");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TIME_SOURCE_UUID, "Time Source");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TIME_UPDATE_CONTROL_POINT_UUID, "Time Update Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TIME_UPDATE_STATE_UUID, "Time Update State");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TIME_WITH_DST_UUID, "Time with DST");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TIME_ZONE_UUID, "Time Zone");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TRUE_WIND_DIRECTION_UUID, "True Wind Direction");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TRUE_WIND_SPEED_UUID, "True Wind Speed");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TWO_ZONE_HEART_RATE_LIMIT_UUID, "Two Zone Heart Rate Limit");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.TX_POWER_LEVEL_UUID, "Tx Power Level");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.UNCERTAINTY_UUID, "Uncertainty");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.UNREAD_ALERT_STATUS_UUID, "Unread Alert Status");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.URI_UUID, "URI");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.USER_CONTROL_POINT_UUID, "User Control Point");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.USER_INDEX_UUID, "User Index");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.UV_INDEX_UUID, "UV Index");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.VO2_MAX_UUID, "VO2 Max");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.WAIST_CIRCUMFERENCE_UUID, "Waist Circumference");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.WEIGHT_UUID, "Weight");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.WEIGHT_MEASUREMENT_UUID, "Weight Measurement");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.WEIGHT_SCALE_FEATURE_UUID, "Weight Scale Feature");
        BLUETOOTH_SPEC_NAMES.put(Characteristics.WIND_CHILL_UUID, "Wind Chill");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.CHARACTERISTIC_AGGREGATE_FORMAT_UUID, "Characteristic Aggregate Format");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.CHARACTERISTIC_EXTENDED_PROPERTIES_UUID, "Characteristic Extended Properties");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.CHARACTERISTIC_PRESENTATION_FORMAT_UUID, "Characteristic Presentation Format");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.CHARACTERISTIC_USER_DESCRIPTION_UUID, "Characteristic User Description");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.CLIENT_CHARACTERISTIC_CONFIGURATION_UUID, "Client Characteristic Configuration");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.ENVIRONMENTAL_SENSING_CONFIGURATION_UUID, "Environmental Sensing Configuration");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.ENVIRONMENTAL_SENSING_MEASUREMENT_UUID, "Environmental Sensing Measurement");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.ENVIRONMENTAL_SENSING_TRIGGER_SETTING_UUID, "Environmental Sensing Trigger Setting");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.EXTERNAL_REPORT_REFERENCE_UUID, "External Report Reference");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.NUMBER_OF_DIGITALS_UUID, "Number of Digitals");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.REPORT_REFERENCE_UUID, "Report Reference");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.SERVER_CHARACTERISTIC_CONFIGURATION_UUID, "Server Characteristic Configuration");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.TIME_TRIGGER_SETTING_UUID, "Time Trigger Setting");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.VALID_RANGE_UUID, "Valid Range");
        BLUETOOTH_SPEC_NAMES.put(Descriptors.VALUE_TRIGGER_SETTING_UUID, "Value Trigger Setting");
    }
}
